package com.example.ecommerce.common

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.math.BigDecimal

open class ApiException(
    val status: Int,
    override val message: String
) : RuntimeException(message)

class BadRequestException(message: String) : ApiException(400, message)
class UnauthorizedException(message: String = "未授权") : ApiException(401, message)
class NotFoundException(message: String) : ApiException(404, message)
class ConflictException(message: String) : ApiException(409, message)

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(ApiException::class)
    fun handleApiException(exception: ApiException): ResponseEntity<ApiResponse<Any?>> {
        return ResponseEntity.status(exception.status)
            .body(ApiResponse.fail(exception.status, exception.message))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ResponseEntity<ApiResponse<Any?>> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.fail(500, exception.message ?: "服务器内部错误"))
    }
}

fun requireText(value: String?, field: String): String {
    return value?.trim()?.takeIf { it.isNotEmpty() }
        ?: throw BadRequestException("$field 不能为空")
}

fun optionalText(value: String?): String? {
    return value?.trim()?.takeIf { it.isNotEmpty() }
}

fun requirePositiveInt(value: Int?, field: String): Int {
    val actual = value ?: throw BadRequestException("$field 不能为空")
    if (actual <= 0) {
        throw BadRequestException("$field 必须大于0")
    }
    return actual
}

fun requireDecimal(value: String?, field: String): BigDecimal {
    val raw = requireText(value, field)
    return try {
        BigDecimal(raw)
    } catch (exception: Exception) {
        throw BadRequestException("$field 格式不正确")
    }
}
