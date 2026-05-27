package com.example.ecommerce.config

import com.example.ecommerce.common.UnauthorizedException
import com.example.ecommerce.service.AuthService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration(proxyBeanMethods = false)
class WebConfig(
    private val authService: AuthService
) : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .exposedHeaders("Authorization")
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(AuthInterceptor(authService))
            .addPathPatterns("/**")
            .excludePathPatterns("/auth/**", "/error")
    }
}

class AuthInterceptor(
    private val authService: AuthService
) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (request.method.equals("OPTIONS", ignoreCase = true)) {
            return true
        }

        val token = request.getHeader("Authorization")
            ?.removePrefix("Bearer ")
            ?.trim()

        if (authService.resolveUserId(token) == null) {
            throw UnauthorizedException("请先登录")
        }

        return true
    }
}
