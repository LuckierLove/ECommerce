package com.example.ecommerce.service

import com.example.ecommerce.common.CURRENT_USER_ID_ATTR
import com.example.ecommerce.common.UnauthorizedException
import com.example.ecommerce.common.requireText
import com.example.ecommerce.model.LoginRequest
import com.example.ecommerce.model.LoginResponse
import com.example.ecommerce.model.RegisterRequest
import com.example.ecommerce.model.User
import com.example.ecommerce.model.UserPayload
import jakarta.servlet.http.HttpServletRequest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class AuthService(
    private val store: InMemoryStore,
    private val jwtService: JwtService,
    private val redisTokenService: RedisTokenService,
    private val jdbcTemplate: JdbcTemplate
) {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun login(request: LoginRequest): LoginResponse {
        val username = requireText(request.username, "用户名")
        val password = requireText(request.password, "密码")
        val account = findUserAccountByUsername(username) ?: throw UnauthorizedException("用户名或密码错误")
        if (!verifyPassword(password, account.passwordHash)) {
            throw UnauthorizedException("用户名或密码错误")
        }
        val token = jwtService.generate(account.id, account.role)
        redisTokenService.saveToken(token, account.id, 240)
        return LoginResponse(token)
    }

    fun register(request: RegisterRequest) {
        store.createUser(
            payload = UserPayload(
                username = request.username,
                email = request.email,
                password = request.password
            )
        )
    }

    fun resolveUserId(token: String?): String? {
        val cachedUserId = redisTokenService.resolveUserId(token)
        if (cachedUserId != null) {
            return cachedUserId
        }
        return jwtService.resolveUserId(token)
    }

    fun currentUser(request: HttpServletRequest): User {
        val userId = request.getAttribute(CURRENT_USER_ID_ATTR) as? String
            ?: resolveUserId(request.getHeader("Authorization")?.removePrefix("Bearer ")?.trim())
            ?: throw UnauthorizedException("请先登录")
        return findUserById(userId)
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    private fun verifyPassword(rawPassword: String, hashedPassword: String): Boolean {
        return hashPassword(rawPassword) == hashedPassword
    }

    private fun findUserAccountByUsername(username: String): com.example.ecommerce.model.UserAccount? {
        val sql = """
            SELECT u.id,
                   u.username,
                   u.email,
                   u.password,
                   COALESCE(r.name, 'user') AS role,
                   u.created_at,
                   u.updated_at
            FROM users u
            LEFT JOIN user_roles ur ON ur.user_id = u.id
            LEFT JOIN roles r ON r.id = ur.role_id
            WHERE u.username = ?
            LIMIT 1
        """.trimIndent()
        return jdbcTemplate.query(sql, { rs, _ ->
            com.example.ecommerce.model.UserAccount(
                id = rs.getString("id"),
                username = rs.getString("username"),
                email = rs.getString("email"),
                passwordHash = rs.getString("password"),
                role = rs.getString("role"),
                createdAt = formatTimestamp(rs.getTimestamp("created_at")?.toLocalDateTime()),
                updatedAt = formatTimestamp(rs.getTimestamp("updated_at")?.toLocalDateTime())
            )
        }, username).firstOrNull()
    }

    private fun findUserById(userId: String): User {
        val sql = """
            SELECT id,
                   username,
                   email,
                   created_at,
                   updated_at
            FROM users
            WHERE id = ?
            LIMIT 1
        """.trimIndent()
        return jdbcTemplate.query(sql, { rs, _ ->
            User(
                id = rs.getString("id"),
                username = rs.getString("username"),
                email = rs.getString("email"),
                createdAt = formatTimestamp(rs.getTimestamp("created_at")?.toLocalDateTime()),
                updatedAt = formatTimestamp(rs.getTimestamp("updated_at")?.toLocalDateTime())
            )
        }, userId).firstOrNull() ?: throw com.example.ecommerce.common.NotFoundException("用户不存在")
    }

    private fun formatTimestamp(value: LocalDateTime?): String {
        return value?.format(formatter) ?: ""
    }
}
