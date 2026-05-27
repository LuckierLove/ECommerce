package com.example.ecommerce.common

import java.util.Comparator

fun normalizePage(page: Int?): Int = page?.takeIf { it > 0 } ?: 1

fun normalizeSize(size: Int?): Int = size?.takeIf { it > 0 }?.coerceAtMost(100) ?: 10

fun <T> paginate(items: List<T>, page: Int, size: Int): PageResponse<T> {
    val fromIndex = ((page - 1) * size).coerceAtMost(items.size)
    val toIndex = (fromIndex + size).coerceAtMost(items.size)
    return PageResponse(
        total = items.size.toLong(),
        page = page,
        size = size,
        list = if (fromIndex >= toIndex) emptyList() else items.subList(fromIndex, toIndex)
    )
}

fun <T> applySorting(items: List<T>, sortOrder: String?, comparator: Comparator<T>): List<T> {
    return if (sortOrder.equals("asc", ignoreCase = true)) {
        items.sortedWith(comparator)
    } else {
        items.sortedWith(comparator.reversed())
    }
}
