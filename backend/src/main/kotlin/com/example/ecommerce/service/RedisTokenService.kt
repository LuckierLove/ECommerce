package com.example.ecommerce.service

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class RedisTokenService(
    private val redisTemplate: StringRedisTemplate
) {
    fun saveToken(token: String, userId: String, ttlMinutes: Long) {
        redisTemplate.opsForValue().set(tokenKey(token), userId, Duration.ofMinutes(ttlMinutes))
    }

    fun resolveUserId(token: String?): String? {
        if (token.isNullOrBlank()) {
            return null
        }
        return redisTemplate.opsForValue().get(tokenKey(token))
    }

    fun removeToken(token: String?) {
        if (!token.isNullOrBlank()) {
            redisTemplate.delete(tokenKey(token))
        }
    }

    private fun tokenKey(token: String): String = "auth:token:$token"
}
