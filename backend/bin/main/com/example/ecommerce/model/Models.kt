package com.example.ecommerce.model

data class UserAccount(
    val id: String,
    val username: String,
    val email: String,
    val passwordHash: String,
    val role: String,
    val createdAt: String,
    val updatedAt: String
)

data class User(
    val id: String,
    val username: String,
    val email: String,
    val createdAt: String,
    val updatedAt: String
)

data class Role(
    val id: String,
    val name: String,
    val createdAt: String,
    val updatedAt: String
)

data class Permission(
    val id: String,
    val name: String,
    val createdAt: String,
    val updatedAt: String
)

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: String,
    val createdAt: String,
    val updatedAt: String
)

data class Merchant(
    val id: String,
    val name: String,
    val createdAt: String,
    val updatedAt: String
)

data class Order(
    val id: String,
    val userId: String,
    val totalAmount: String,
    val subtotal: String,
    val taxAmount: String,
    val shippingCost: String,
    val currency: String,
    val status: String,
    val paymentStatus: String,
    val itemsCount: Int,
    val createdAt: String,
    val updatedAt: String,
    val billingAddressId: String? = null,
    val shippingAddressId: String? = null,
    val shippingMethod: String? = null,
    val trackingNumber: String? = null,
    val note: String? = null
)

data class OrderDetail(
    val id: String,
    val userId: String,
    val totalAmount: String,
    val subtotal: String,
    val taxAmount: String,
    val shippingCost: String,
    val currency: String,
    val status: String,
    val paymentStatus: String,
    val createdAt: String,
    val updatedAt: String,
    val items: List<OrderItem>
)

data class OrderItemPayload(
    val productId: String? = null,
    val quantity: Int? = null,
    val price: String? = null,
    val sku: String? = null
)

data class OrderPayload(
    val userId: String? = null,
    val totalAmount: String? = null,
    val items: List<OrderItemPayload>? = null
    ,
    val subtotal: String? = null,
    val shippingAddressId: String? = null,
    val billingAddressId: String? = null,
    val couponCodes: List<String>? = null
    ,
    val paymentMethod: Int? = null,
    val autoPay: Boolean? = null,

    val status: String? = null,
    val paymentStatus: String? = null
)

data class OrderItem(
    val id: String,
    val orderId: String,
    val productId: String,
    val quantity: Int,
    val price: String,
    val sku: String? = null,
    val name: String? = null,
    val unitPrice: String? = null,
    val taxAmount: String? = null,
    val discountAmount: String? = null,
    val subtotal: String? = null,
    val createdAt: String,
    val updatedAt: String
)

data class Cart(
    val id: String,
    val userId: String,
    val createdAt: String,
    val updatedAt: String
)

data class CartItem(
    val id: String,
    val cartId: String,
    val productId: String,
    val quantity: Int,
    val unitPrice: String? = null,
    val selectedOptions: String? = null,
    val subtotal: String? = null,
    val createdAt: String,
    val updatedAt: String
)

data class Payment(
    val id: String,
    val orderId: String,
    val amount: String,
    val paymentMethod: Int,
    val createdAt: String,
    val updatedAt: String
)

data class Address(
    val id: String,
    val userId: String,
    val street: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val country: String,
    val createdAt: String,
    val updatedAt: String
)

data class Inventory(
    val id: String,
    val productId: String,
    val quantity: Int,
    val createdAt: String,
    val updatedAt: String
)

data class Coupon(
    val id: String,
    val code: String,
    val discountAmount: String,
    val createdAt: String,
    val updatedAt: String
)

data class LogRecord(
    val id: String,
    val userId: String,
    val logType: Int,
    val message: String,
    val createdAt: String,
    val updatedAt: String
)

data class OrderSubmittedEvent(
    val orderId: String,
    val userId: String,
    val totalAmount: String,
    val items: List<OrderItemPayload>,
    val merchantIds: List<String>,
    val createdAt: String
)

data class MerchantOrderNotice(
    val id: String,
    val merchantId: String,
    val orderId: String,
    val userId: String,
    val message: String,
    val createdAt: String
)

data class LoginRequest(
    val username: String? = null,
    val password: String? = null
)

data class RegisterRequest(
    val username: String? = null,
    val email: String? = null,
    val password: String? = null
)

data class LoginResponse(
    val token: String
)

data class UserPayload(
    val username: String? = null,
    val email: String? = null,
    val password: String? = null
)

data class NamedPayload(
    val name: String? = null
)

data class ProductPayload(
    val name: String? = null,
    val description: String? = null,
    val price: String? = null
)

data class CartItemPayload(
    val cartId: String? = null,
    val productId: String? = null,
    val quantity: Int? = null
    ,
    val unitPrice: String? = null,
    val selectedOptions: String? = null
)

data class CouponPayload(
    val code: String? = null,
    val discountAmount: String? = null
)

data class AddressPayload(
    val userId: String? = null,
    val street: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zipCode: String? = null,
    val country: String? = null
)

data class InventoryPayload(
    val productId: String? = null,
    val quantity: Int? = null
)

data class PaymentPayload(
    val orderId: String? = null,
    val amount: String? = null,
    val paymentMethod: Int? = null
)

data class LogPayload(
    val userId: String? = null,
    val logType: Int? = null,
    val message: String? = null
)

fun UserAccount.toUser(): User = User(
    id = id,
    username = username,
    email = email,
    createdAt = createdAt,
    updatedAt = updatedAt
)
