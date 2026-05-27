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
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val store: InMemoryStore,
    private val jwtService: JwtService,
    private val redisTokenService: RedisTokenService
) {
    fun login(request: LoginRequest): LoginResponse {
        val username = requireText(request.username, "用户名")
        val password = requireText(request.password, "密码")
        val account = store.findUserByUsername(username) ?: throw UnauthorizedException("用户名或密码错误")
        store.login(account.id, password)
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
        return store.findUserById(userId)
    }
}
