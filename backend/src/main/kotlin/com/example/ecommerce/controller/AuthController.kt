package com.example.ecommerce.controller

import com.example.ecommerce.common.ApiResponse
import com.example.ecommerce.model.LoginRequest
import com.example.ecommerce.model.RegisterRequest
import com.example.ecommerce.service.AuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ApiResponse<Any?> {
        return ApiResponse.ok(authService.login(request))
    }

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ApiResponse<Nothing?> {
        authService.register(request)
        return ApiResponse.ok(null, "注册成功")
    }
}
