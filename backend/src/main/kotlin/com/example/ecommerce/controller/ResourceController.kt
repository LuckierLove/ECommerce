package com.example.ecommerce.controller

import com.example.ecommerce.common.ApiResponse
import com.example.ecommerce.model.*
import com.example.ecommerce.service.AuthService
import com.example.ecommerce.service.InMemoryStore
import com.example.ecommerce.service.MerchantNotificationService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class ResourceController(
    private val store: InMemoryStore,
    private val authService: AuthService,
    private val merchantNotificationService: MerchantNotificationService
) {
    @GetMapping("/users/me")
    fun me(request: HttpServletRequest): ApiResponse<User> {
        return ApiResponse.ok(authService.currentUser(request))
    }

    @GetMapping("/users")
    fun listUsers(
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(required = false) sortOrder: String?
    ): ApiResponse<Any?> {
        return ApiResponse.ok(store.listUsers(page, size, keyword, sortBy, sortOrder))
    }

    @GetMapping("/users/{id}")
    fun getUser(@PathVariable id: String): ApiResponse<User> = ApiResponse.ok(store.findUserById(id))

    @PostMapping("/users")
    fun createUser(@RequestBody payload: UserPayload): ApiResponse<User> = ApiResponse.ok(store.createUser(payload))

    @PutMapping("/users/{id}")
    fun updateUser(@PathVariable id: String, @RequestBody payload: UserPayload): ApiResponse<User> =
        ApiResponse.ok(store.updateUser(id, payload))

    @DeleteMapping("/users/{id}")
    fun deleteUser(@PathVariable id: String): ApiResponse<Nothing?> {
        store.deleteUser(id)
        return ApiResponse.ok(null, "删除成功")
    }

    @GetMapping("/roles")
    fun listRoles(
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(required = false) sortOrder: String?
    ): ApiResponse<Any?> = ApiResponse.ok(store.listRoles(page, size, keyword, sortBy, sortOrder))

    @GetMapping("/roles/{id}")
    fun getRole(@PathVariable id: String): ApiResponse<Role> = ApiResponse.ok(store.getRole(id))

    @PostMapping("/roles")
    fun createRole(@RequestBody payload: NamedPayload): ApiResponse<Role> = ApiResponse.ok(store.createRole(payload))

    @PutMapping("/roles/{id}")
    fun updateRole(@PathVariable id: String, @RequestBody payload: NamedPayload): ApiResponse<Role> =
        ApiResponse.ok(store.updateRole(id, payload))

    @DeleteMapping("/roles/{id}")
    fun deleteRole(@PathVariable id: String): ApiResponse<Nothing?> {
        store.deleteRole(id)
        return ApiResponse.ok(null, "删除成功")
    }

    @GetMapping("/permissions")
    fun listPermissions(
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(required = false) sortOrder: String?
    ): ApiResponse<Any?> = ApiResponse.ok(store.listPermissions(page, size, keyword, sortBy, sortOrder))

    @GetMapping("/permissions/{id}")
    fun getPermission(@PathVariable id: String): ApiResponse<Permission> = ApiResponse.ok(store.getPermission(id))

    @PostMapping("/permissions")
    fun createPermission(@RequestBody payload: NamedPayload): ApiResponse<Permission> =
        ApiResponse.ok(store.createPermission(payload))

    @PutMapping("/permissions/{id}")
    fun updatePermission(@PathVariable id: String, @RequestBody payload: NamedPayload): ApiResponse<Permission> =
        ApiResponse.ok(store.updatePermission(id, payload))

    @DeleteMapping("/permissions/{id}")
    fun deletePermission(@PathVariable id: String): ApiResponse<Nothing?> {
        store.deletePermission(id)
        return ApiResponse.ok(null, "删除成功")
    }

    @GetMapping("/products")
    fun listProducts(
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false) minPrice: String?,
        @RequestParam(required = false) maxPrice: String?,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(required = false) sortOrder: String?
    ): ApiResponse<Any?> = ApiResponse.ok(store.listProducts(page, size, keyword, minPrice, maxPrice, sortBy, sortOrder))

    @GetMapping("/products/{id}")
    fun getProduct(@PathVariable id: String): ApiResponse<Product> = ApiResponse.ok(store.getProduct(id))

    @PostMapping("/products")
    fun createProduct(@RequestBody payload: ProductPayload): ApiResponse<Product> = ApiResponse.ok(store.createProduct(payload))

    @PutMapping("/products/{id}")
    fun updateProduct(@PathVariable id: String, @RequestBody payload: ProductPayload): ApiResponse<Product> =
        ApiResponse.ok(store.updateProduct(id, payload))

    @DeleteMapping("/products/{id}")
    fun deleteProduct(@PathVariable id: String): ApiResponse<Nothing?> {
        store.deleteProduct(id)
        return ApiResponse.ok(null, "删除成功")
    }

    @GetMapping("/merchants")
    fun listMerchants(
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(required = false) sortOrder: String?
    ): ApiResponse<Any?> = ApiResponse.ok(store.listMerchants(page, size, keyword, sortBy, sortOrder))

    @GetMapping("/merchants/{id}")
    fun getMerchant(@PathVariable id: String): ApiResponse<Merchant> = ApiResponse.ok(store.getMerchant(id))

    @PostMapping("/merchants")
    fun createMerchant(@RequestBody payload: NamedPayload): ApiResponse<Merchant> = ApiResponse.ok(store.createMerchant(payload))

    @PutMapping("/merchants/{id}")
    fun updateMerchant(@PathVariable id: String, @RequestBody payload: NamedPayload): ApiResponse<Merchant> =
        ApiResponse.ok(store.updateMerchant(id, payload))

    @DeleteMapping("/merchants/{id}")
    fun deleteMerchant(@PathVariable id: String): ApiResponse<Nothing?> {
        store.deleteMerchant(id)
        return ApiResponse.ok(null, "删除成功")
    }

    @GetMapping("/orders")
    fun listOrders(
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?,
        @RequestParam(required = false) userId: String?,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(required = false) sortOrder: String?
    ): ApiResponse<Any?> = ApiResponse.ok(store.listOrders(page, size, userId, sortBy, sortOrder))

    @GetMapping("/orders/{id}")
    fun getOrder(@PathVariable id: String): ApiResponse<Order> = ApiResponse.ok(store.getOrder(id))

    @PostMapping("/orders")
    fun createOrder(@RequestBody payload: OrderPayload): ApiResponse<Order> = ApiResponse.ok(store.createOrder(payload))

    @PutMapping("/orders/{id}")
    fun updateOrder(@PathVariable id: String, @RequestBody payload: OrderPayload): ApiResponse<Order> =
        ApiResponse.ok(store.updateOrder(id, payload))

    @DeleteMapping("/orders/{id}")
    fun deleteOrder(@PathVariable id: String): ApiResponse<Nothing?> {
        store.deleteOrder(id)
        return ApiResponse.ok(null, "删除成功")
    }

    @GetMapping("/merchant/notifications")
    fun listMerchantNotifications(
        request: HttpServletRequest,
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?
    ): ApiResponse<Any?> {
        val currentUser = authService.currentUser(request)
        val merchantId = store.findMerchantIdByUserId(currentUser.id)
        return ApiResponse.ok(
            if (merchantId == null) {
                emptyList<Any>()
            } else {
                merchantNotificationService.listNotices(merchantId, page, size)
            }
        )
    }

    @GetMapping("/carts")
    fun listCarts(
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?,
        @RequestParam(required = false) userId: String?
    ): ApiResponse<Any?> = ApiResponse.ok(store.listCarts(page, size, userId))

    @GetMapping("/cart-items")
    fun listCartItems(
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?,
        @RequestParam(required = false) cartId: String?,
        @RequestParam(required = false) productId: String?
    ): ApiResponse<Any?> = ApiResponse.ok(store.listCartItems(page, size, cartId, productId))

    @PostMapping("/cart-items")
    fun createCartItem(@RequestBody payload: CartItemPayload): ApiResponse<CartItem> =
        ApiResponse.ok(store.createCartItem(payload))

    @PutMapping("/cart-items/{id}")
    fun updateCartItem(@PathVariable id: String, @RequestBody payload: CartItemPayload): ApiResponse<CartItem> =
        ApiResponse.ok(store.updateCartItem(id, payload))

    @DeleteMapping("/cart-items/{id}")
    fun deleteCartItem(@PathVariable id: String): ApiResponse<Nothing?> {
        store.deleteCartItem(id)
        return ApiResponse.ok(null, "删除成功")
    }

    @GetMapping("/coupons")
    fun listCoupons(
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?,
        @RequestParam(required = false) code: String?,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(required = false) sortOrder: String?
    ): ApiResponse<Any?> = ApiResponse.ok(store.listCoupons(page, size, code, sortBy, sortOrder))

    @PostMapping("/coupons")
    fun createCoupon(@RequestBody payload: CouponPayload): ApiResponse<Coupon> = ApiResponse.ok(store.createCoupon(payload))

    @PutMapping("/coupons/{id}")
    fun updateCoupon(@PathVariable id: String, @RequestBody payload: CouponPayload): ApiResponse<Coupon> =
        ApiResponse.ok(store.updateCoupon(id, payload))

    @DeleteMapping("/coupons/{id}")
    fun deleteCoupon(@PathVariable id: String): ApiResponse<Nothing?> {
        store.deleteCoupon(id)
        return ApiResponse.ok(null, "删除成功")
    }

    @GetMapping("/addresses")
    fun listAddresses(
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?,
        @RequestParam(required = false) userId: String?,
        @RequestParam(required = false) city: String?,
        @RequestParam(required = false) state: String?,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(required = false) sortOrder: String?
    ): ApiResponse<Any?> = ApiResponse.ok(store.listAddresses(page, size, userId, city, state, sortBy, sortOrder))

    @PostMapping("/addresses")
    fun createAddress(@RequestBody payload: AddressPayload): ApiResponse<Address> = ApiResponse.ok(store.createAddress(payload))

    @PutMapping("/addresses/{id}")
    fun updateAddress(@PathVariable id: String, @RequestBody payload: AddressPayload): ApiResponse<Address> =
        ApiResponse.ok(store.updateAddress(id, payload))

    @DeleteMapping("/addresses/{id}")
    fun deleteAddress(@PathVariable id: String): ApiResponse<Nothing?> {
        store.deleteAddress(id)
        return ApiResponse.ok(null, "删除成功")
    }

    @GetMapping("/inventory")
    fun listInventory(
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?,
        @RequestParam(required = false) productId: String?,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(required = false) sortOrder: String?
    ): ApiResponse<Any?> = ApiResponse.ok(store.listInventory(page, size, productId, sortBy, sortOrder))

    @PostMapping("/inventory")
    fun createInventory(@RequestBody payload: InventoryPayload): ApiResponse<Inventory> =
        ApiResponse.ok(store.createInventory(payload))

    @PutMapping("/inventory/{id}")
    fun updateInventory(@PathVariable id: String, @RequestBody payload: InventoryPayload): ApiResponse<Inventory> =
        ApiResponse.ok(store.updateInventory(id, payload))

    @DeleteMapping("/inventory/{id}")
    fun deleteInventory(@PathVariable id: String): ApiResponse<Nothing?> {
        store.deleteInventory(id)
        return ApiResponse.ok(null, "删除成功")
    }

    @GetMapping("/payments")
    fun listPayments(
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?,
        @RequestParam(required = false) orderId: String?,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(required = false) sortOrder: String?
    ): ApiResponse<Any?> = ApiResponse.ok(store.listPayments(page, size, orderId, sortBy, sortOrder))

    @PostMapping("/payments")
    fun createPayment(@RequestBody payload: PaymentPayload): ApiResponse<Payment> = ApiResponse.ok(store.createPayment(payload))

    @PutMapping("/payments/{id}")
    fun updatePayment(@PathVariable id: String, @RequestBody payload: PaymentPayload): ApiResponse<Payment> =
        ApiResponse.ok(store.updatePayment(id, payload))

    @DeleteMapping("/payments/{id}")
    fun deletePayment(@PathVariable id: String): ApiResponse<Nothing?> {
        store.deletePayment(id)
        return ApiResponse.ok(null, "删除成功")
    }

    @GetMapping("/logs")
    fun listLogs(
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?,
        @RequestParam(required = false) userId: String?,
        @RequestParam(required = false) logType: Int?,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(required = false) sortOrder: String?
    ): ApiResponse<Any?> = ApiResponse.ok(store.listLogs(page, size, userId, logType, sortBy, sortOrder))

    @PostMapping("/logs")
    fun createLog(@RequestBody payload: LogPayload): ApiResponse<LogRecord> = ApiResponse.ok(store.createLog(payload))

    @PutMapping("/logs/{id}")
    fun updateLog(@PathVariable id: String, @RequestBody payload: LogPayload): ApiResponse<LogRecord> =
        ApiResponse.ok(store.updateLog(id, payload))

    @DeleteMapping("/logs/{id}")
    fun deleteLog(@PathVariable id: String): ApiResponse<Nothing?> {
        store.deleteLog(id)
        return ApiResponse.ok(null, "删除成功")
    }
}
