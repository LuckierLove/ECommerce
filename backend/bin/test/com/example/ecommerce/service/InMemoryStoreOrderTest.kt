package com.example.ecommerce.service

import com.example.ecommerce.model.OrderPayload
import com.example.ecommerce.model.OrderItemPayload
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.jdbc.core.JdbcTemplate

class InMemoryStoreOrderTest {

    @Test
    fun `createOrder deducts inventory and creates payment when autoPay`() {
        val jdbc = mock<JdbcTemplate>()
        val publisher = mock<OrderEventPublisher>()

        // stub jdbcTemplate calls used in createOrder
        whenever(jdbc.queryForObject(any<String>(), any<Class<Long>>(), any())).thenReturn(1L)
        whenever(jdbc.update(any<String>(), *arrayOfNulls<Any>(0))).thenReturn(1)
        whenever(jdbc.update(any<String>(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(1)

        val store = InMemoryStore(publisher, jdbc)

        // create a user
        val user = store.createUser(com.example.ecommerce.model.UserPayload(username = "tuser", email = "t@ex.com", password = "pwd"))
        // create a product
        val product = store.createProduct(com.example.ecommerce.model.ProductPayload(name = "tprod", description = "desc", price = "100.00"))
        // create inventory for product
        store.createInventory(com.example.ecommerce.model.InventoryPayload(productId = product.id, quantity = 5))

        // get cart id for user
        val cartsPage = store.listCarts(1, 10, user.id)
        val cartId = cartsPage.list.first().id

        // add cart item
        store.createCartItem(com.example.ecommerce.model.CartItemPayload(cartId = cartId, productId = product.id, quantity = 2))

        // prepare order payload
        val payload = OrderPayload(userId = user.id, totalAmount = "226.00", items = listOf(OrderItemPayload(productId = product.id, quantity = 2)), autoPay = true)

        val order = store.createOrder(payload)

        // payment should be created
        val payments = store.listPayments(1, 10, order.id, null, null)
        println("DEBUG: payments.total=${'$'}{payments.total}, payments.list.size=${'$'}{payments.list.size}")
        assertTrue(payments.total >= 1)

        // inventory should be deducted (5 - 2 = 3)
        val invPage = store.listInventory(1, 10, product.id, null, null)
        val totalRemaining = invPage.list.sumOf { it.quantity }
        assertEquals(3, totalRemaining)

        // order payment status should be PAID
        assertEquals("PAID", order.paymentStatus)
    }
}
