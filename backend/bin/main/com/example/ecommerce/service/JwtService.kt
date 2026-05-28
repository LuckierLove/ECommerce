package com.example.ecommerce.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.util.Date

@Service
class JwtService(
    @Value("\${app.jwt.secret}") private val secret: String,
    @Value("\${app.jwt.expiration-minutes}") private val expirationMinutes: Long
) {
    private val key by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))
    }

    fun generate(userId: String, role: String): String {
        val now = Date()
        val expiry = Date(now.time + expirationMinutes * 60_000)
        return Jwts.builder()
            .subject(userId)
            .claim("role", role)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact()
    }

    fun resolveUserId(token: String?): String? {
        if (token.isNullOrBlank()) {
            return null
        }

        return try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
                .subject
        } catch (_: Exception) {
            null
        }
    }
}
