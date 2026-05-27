package com.example.ecommerce.common

data class ApiResponse<out T>(
    val code: Int,
    val message: String,
    val data: T?
) {
    companion object {
        fun <T> ok(data: T?, message: String = "请求成功"): ApiResponse<T> = ApiResponse(200, message, data)
        fun <T> fail(code: Int, message: String): ApiResponse<T> = ApiResponse(code, message, null)
    }
}

data class PageResponse<T>(
    val total: Long,
    val page: Int,
    val size: Int,
    val list: List<T>
)
