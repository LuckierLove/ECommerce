package com.example.ecommerce.service

import com.example.ecommerce.common.BadRequestException
import com.example.ecommerce.common.ConflictException
import com.example.ecommerce.common.NotFoundException
import com.example.ecommerce.common.PageResponse
import com.example.ecommerce.common.applySorting
import com.example.ecommerce.common.normalizePage
import com.example.ecommerce.common.normalizeSize
import com.example.ecommerce.common.paginate
import com.example.ecommerce.common.optionalText
import com.example.ecommerce.common.requireDecimal
import com.example.ecommerce.common.requirePositiveInt
import com.example.ecommerce.common.requireText
import com.example.ecommerce.common.UnauthorizedException
import com.example.ecommerce.model.*
import org.springframework.stereotype.Component
import org.springframework.jdbc.core.JdbcTemplate
import java.math.BigDecimal
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Component
class InMemoryStore {
    private val idSequence = AtomicLong(1000)
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    private val tokens = ConcurrentHashMap<String, String>()

    private val users = LinkedHashMap<String, UserAccount>()
    private val roles = LinkedHashMap<String, Role>()
    private val permissions = LinkedHashMap<String, Permission>()
    private val products = LinkedHashMap<String, Product>()
    private val merchants = LinkedHashMap<String, Merchant>()
    private val merchantUsers = LinkedHashMap<String, String>()
    private val merchantProducts = LinkedHashMap<String, String>()
    private val orders = LinkedHashMap<String, Order>()
    private val orderItems = LinkedHashMap<String, OrderItem>()
    private val carts = LinkedHashMap<String, Cart>()
    private val cartItems = LinkedHashMap<String, CartItem>()
    private val payments = LinkedHashMap<String, Payment>()
    private val addresses = LinkedHashMap<String, Address>()
    private val inventory = LinkedHashMap<String, Inventory>()
    private val coupons = LinkedHashMap<String, Coupon>()
    private val logs = LinkedHashMap<String, LogRecord>()

    private val orderEventPublisher: OrderEventPublisher
    private val jdbcTemplate: JdbcTemplate

    constructor(orderEventPublisher: OrderEventPublisher, jdbcTemplate: JdbcTemplate) {
        this.orderEventPublisher = orderEventPublisher
        this.jdbcTemplate = jdbcTemplate
        seedData()
    }

    private fun now(): String = LocalDateTime.now().format(formatter)

    private fun nextId(): String = idSequence.incrementAndGet().toString()

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    private fun verifyPassword(rawPassword: String, hashedPassword: String): Boolean {
        return hashPassword(rawPassword) == hashedPassword
    }

    private fun seedData() {
        val timestamp = now()
        listOf(
            Role(nextId(), "admin", timestamp, timestamp),
            Role(nextId(), "user", timestamp, timestamp),
            Role(nextId(), "merchant", timestamp, timestamp)
        ).forEach { roles[it.id] = it }

        listOf("users.read", "users.write", "products.read", "orders.read", "logs.read").forEach { name ->
            val permission = Permission(nextId(), name, timestamp, timestamp)
            permissions[permission.id] = permission
        }

        val admin = createSeedUser("admin", "admin@example.com", "admin123", "admin")
        val alice = createSeedUser("alice", "alice@example.com", "alice123", "user")
        val merchant = createSeedUser("merchant1", "merchant@example.com", "merchant123", "merchant")
        val bob = createSeedUser("bob", "bob@example.com", "bob123", "user")

        createSeedCart(alice.id)
        createSeedCart(merchant.id)
        createSeedCart(bob.id)

        listOf(
            Merchant(nextId(), "星火商贸", timestamp, timestamp),
            Merchant(nextId(), "云集数码", timestamp, timestamp),
            Merchant(nextId(), "南风优选", timestamp, timestamp)
        ).forEach { merchants[it.id] = it }

        val merchantIds = merchants.keys.toList()
        merchantUsers[merchant.id] = merchantIds.first()

        val seedProducts = listOf(
            Product(nextId(), "轻薄笔记本", "14英寸办公本", "4999.00", timestamp, timestamp),
            Product(nextId(), "无线鼠标", "静音便携", "79.90", timestamp, timestamp),
            Product(nextId(), "机械键盘", "RGB背光键盘", "299.00", timestamp, timestamp),
            Product(nextId(), "蓝牙耳机", "降噪通勤耳机", "359.00", timestamp, timestamp),
            Product(nextId(), "显示器", "27英寸2K显示器", "1299.00", timestamp, timestamp)
        )
        seedProducts.forEachIndexed { index, product ->
            products[product.id] = product
            merchantProducts[product.id] = merchantIds[index % merchantIds.size]
            val inventoryId = nextId()
            inventory[inventoryId] = Inventory(inventoryId, product.id, 100, timestamp, timestamp)
        }

        listOf(
            Coupon(nextId(), "WELCOME10", "10.00", timestamp, timestamp),
            Coupon(nextId(), "VIP50", "50.00", timestamp, timestamp)
        ).forEach { coupons[it.id] = it }

        val aliceCart = carts.values.first { it.userId == alice.id }
        val cartItemOneId = nextId()
        cartItems[cartItemOneId] = CartItem(cartItemOneId, aliceCart.id, seedProducts.first().id, 1, timestamp, timestamp)
        val cartItemTwoId = nextId()
        cartItems[cartItemTwoId] = CartItem(cartItemTwoId, aliceCart.id, seedProducts[1].id, 2, timestamp, timestamp)

        val order = Order(nextId(), alice.id, "5278.90", timestamp, timestamp)
        orders[order.id] = order
        val orderItemOneId = nextId()
        orderItems[orderItemOneId] = OrderItem(orderItemOneId, order.id, seedProducts.first().id, 1, "4999.00", timestamp, timestamp)
        val orderItemTwoId = nextId()
        orderItems[orderItemTwoId] = OrderItem(orderItemTwoId, order.id, seedProducts[1].id, 2, "79.90", timestamp, timestamp)

        val paymentId = nextId()
        payments[paymentId] = Payment(paymentId, order.id, order.totalAmount, 1, timestamp, timestamp)

        val addressOneId = nextId()
        addresses[addressOneId] = Address(addressOneId, alice.id, "中关村大街1号", "北京", "北京", "100080", "中国", timestamp, timestamp)
        val addressTwoId = nextId()
        addresses[addressTwoId] = Address(addressTwoId, merchant.id, "滨江路88号", "杭州", "浙江", "310000", "中国", timestamp, timestamp)

        val logOneId = nextId()
        logs[logOneId] = LogRecord(logOneId, admin.id, 1, "管理员登录", timestamp, timestamp)
        val logTwoId = nextId()
        logs[logTwoId] = LogRecord(logTwoId, alice.id, 2, "浏览商品列表", timestamp, timestamp)
        val logThreeId = nextId()
        logs[logThreeId] = LogRecord(logThreeId, merchant.id, 2, "发布商品活动", timestamp, timestamp)
    }

    private fun createSeedUser(username: String, email: String, password: String, role: String): UserAccount {
        val timestamp = now()
        val user = UserAccount(nextId(), username, email, hashPassword(password), role, timestamp, timestamp)
        users[user.id] = user
        return user
    }

    private fun createSeedCart(userId: String): Cart {
        val timestamp = now()
        val cart = Cart(nextId(), userId, timestamp, timestamp)
        carts[cart.id] = cart
        return cart
    }

    fun resolveToken(token: String?): String? = token?.let { tokens[it] }

    fun issueToken(userId: String): String {
        val token = "token-$userId-${nextId()}"
        tokens[token] = userId
        return token
    }

    fun login(userId: String, rawPassword: String): String {
        val account = users[userId] ?: throw UnauthorizedException("用户名或密码错误")
        if (!verifyPassword(rawPassword, account.passwordHash)) {
            throw UnauthorizedException("用户名或密码错误")
        }
        return issueToken(userId)
    }

    fun getCurrentUser(token: String?): User {
        val userId = resolveToken(token) ?: throw NotFoundException("用户不存在")
        return users[userId]?.toUser() ?: throw NotFoundException("用户不存在")
    }

    fun findUserAccountById(id: String): UserAccount? = users[id]

    fun findUserById(id: String): User = users[id]?.toUser() ?: throw NotFoundException("用户不存在")

    fun findUserByUsername(username: String): UserAccount? =
        users.values.firstOrNull { it.username.equals(username, ignoreCase = true) }

    fun findMerchantIdByUserId(userId: String): String? = merchantUsers[userId]

    fun findMerchantIdByProductId(productId: String): String? = merchantProducts[productId]

    fun createUser(payload: UserPayload, role: String = "user"): User {
        val username = requireText(payload.username, "用户名")
        val email = requireText(payload.email, "邮箱")
        val password = requireText(payload.password, "密码")

        if (users.values.any { it.username.equals(username, ignoreCase = true) }) {
            throw ConflictException("用户名已存在")
        }
        if (users.values.any { it.email.equals(email, ignoreCase = true) }) {
            throw ConflictException("邮箱已存在")
        }

        val timestamp = now()
        val user = UserAccount(nextId(), username, email, hashPassword(password), role, timestamp, timestamp)
        users[user.id] = user
        ensureCart(user.id)
        return user.toUser()
    }

    fun updateUser(id: String, payload: UserPayload): User {
        val current = users[id] ?: throw NotFoundException("用户不存在")
        val username = optionalText(payload.username) ?: current.username
        val email = optionalText(payload.email) ?: current.email

        if (username != current.username && users.values.any { it.username.equals(username, ignoreCase = true) }) {
            throw ConflictException("用户名已存在")
        }
        if (email != current.email && users.values.any { it.email.equals(email, ignoreCase = true) }) {
            throw ConflictException("邮箱已存在")
        }

        val passwordHash = payload.password?.trim()?.takeIf { it.isNotEmpty() }?.let { hashPassword(it) } ?: current.passwordHash
        val updated = current.copy(
            username = username,
            email = email,
            passwordHash = passwordHash,
            updatedAt = now()
        )
        users[id] = updated
        return updated.toUser()
    }

    fun deleteUser(id: String) {
        if (users.remove(id) == null) {
            throw NotFoundException("用户不存在")
        }
        tokens.entries.removeIf { it.value == id }
        carts.entries.removeIf { it.value.userId == id }
        addresses.entries.removeIf { it.value.userId == id }
        orders.entries.removeIf { it.value.userId == id }
        logs.entries.removeIf { it.value.userId == id }
    }

    fun listUsers(page: Int?, size: Int?, keyword: String?, sortBy: String?, sortOrder: String?): PageResponse<User> {
        val pageNumber = normalizePage(page)
        val pageSize = normalizeSize(size)
        val filtered = users.values.map { it.toUser() }.filter {
            keyword.isNullOrBlank() || it.username.contains(keyword, ignoreCase = true) || it.email.contains(keyword, ignoreCase = true)
        }
        val comparator = when (sortBy?.lowercase()) {
            "username" -> compareBy<User> { it.username.lowercase() }
            "email" -> compareBy<User> { it.email.lowercase() }
            else -> compareBy<User> { it.createdAt }
        }
        return paginate(applySorting(filtered, sortOrder, comparator), pageNumber, pageSize)
    }

    fun listRoles(page: Int?, size: Int?, keyword: String?, sortBy: String?, sortOrder: String?): PageResponse<Role> {
        val pageNumber = normalizePage(page)
        val pageSize = normalizeSize(size)
        val filtered = roles.values.filter { keyword.isNullOrBlank() || it.name.contains(keyword, ignoreCase = true) }
        val comparator = when (sortBy?.lowercase()) {
            "name" -> compareBy<Role> { it.name.lowercase() }
            else -> compareBy<Role> { it.createdAt }
        }
        return paginate(applySorting(filtered, sortOrder, comparator), pageNumber, pageSize)
    }

    fun getRole(id: String): Role = roles[id] ?: throw NotFoundException("角色不存在")

    fun createRole(payload: NamedPayload): Role {
        val name = requireText(payload.name, "角色名称")
        if (roles.values.any { it.name.equals(name, ignoreCase = true) }) {
            throw ConflictException("角色名称已存在")
        }
        val timestamp = now()
        val role = Role(nextId(), name, timestamp, timestamp)
        roles[role.id] = role
        return role
    }

    fun updateRole(id: String, payload: NamedPayload): Role {
        val current = roles[id] ?: throw NotFoundException("角色不存在")
        val name = requireText(payload.name, "角色名称")
        if (name != current.name && roles.values.any { it.name.equals(name, ignoreCase = true) }) {
            throw ConflictException("角色名称已存在")
        }
        val updated = current.copy(name = name, updatedAt = now())
        roles[id] = updated
        return updated
    }

    fun deleteRole(id: String) {
        if (roles.remove(id) == null) {
            throw NotFoundException("角色不存在")
        }
    }

    fun listPermissions(page: Int?, size: Int?, keyword: String?, sortBy: String?, sortOrder: String?): PageResponse<Permission> {
        val pageNumber = normalizePage(page)
        val pageSize = normalizeSize(size)
        val filtered = permissions.values.filter { keyword.isNullOrBlank() || it.name.contains(keyword, ignoreCase = true) }
        val comparator = when (sortBy?.lowercase()) {
            "name" -> compareBy<Permission> { it.name.lowercase() }
            else -> compareBy<Permission> { it.createdAt }
        }
        return paginate(applySorting(filtered, sortOrder, comparator), pageNumber, pageSize)
    }

    fun getPermission(id: String): Permission = permissions[id] ?: throw NotFoundException("权限不存在")

    fun createPermission(payload: NamedPayload): Permission {
        val name = requireText(payload.name, "权限名称")
        if (permissions.values.any { it.name.equals(name, ignoreCase = true) }) {
            throw ConflictException("权限名称已存在")
        }
        val timestamp = now()
        val permission = Permission(nextId(), name, timestamp, timestamp)
        permissions[permission.id] = permission
        return permission
    }

    fun updatePermission(id: String, payload: NamedPayload): Permission {
        val current = permissions[id] ?: throw NotFoundException("权限不存在")
        val name = requireText(payload.name, "权限名称")
        if (name != current.name && permissions.values.any { it.name.equals(name, ignoreCase = true) }) {
            throw ConflictException("权限名称已存在")
        }
        val updated = current.copy(name = name, updatedAt = now())
        permissions[id] = updated
        return updated
    }

    fun deletePermission(id: String) {
        if (permissions.remove(id) == null) {
            throw NotFoundException("权限不存在")
        }
    }

    fun listProducts(page: Int?, size: Int?, keyword: String?, minPrice: String?, maxPrice: String?, sortBy: String?, sortOrder: String?): PageResponse<Product> {
        val pageNumber = normalizePage(page)
        val pageSize = normalizeSize(size)
        val min = minPrice?.let { runCatching { BigDecimal(it) }.getOrNull() }
        val max = maxPrice?.let { runCatching { BigDecimal(it) }.getOrNull() }
        val filtered = products.values.filter { product ->
            val matchesKeyword = keyword.isNullOrBlank() || product.name.contains(keyword, ignoreCase = true) || product.description.contains(keyword, ignoreCase = true)
            val price = BigDecimal(product.price)
            val matchesMin = min == null || price >= min
            val matchesMax = max == null || price <= max
            matchesKeyword && matchesMin && matchesMax
        }
        val comparator = when (sortBy?.lowercase()) {
            "name" -> compareBy<Product> { it.name.lowercase() }
            "price" -> compareBy<Product> { BigDecimal(it.price) }
            else -> compareBy<Product> { it.createdAt }
        }
        return paginate(applySorting(filtered, sortOrder, comparator), pageNumber, pageSize)
    }

    fun getProduct(id: String): Product = products[id] ?: throw NotFoundException("商品不存在")

    fun createProduct(payload: ProductPayload): Product {
        val name = requireText(payload.name, "商品名称")
        val price = requireDecimal(payload.price, "商品价格")
        if (price < BigDecimal.ZERO) {
            throw BadRequestException("商品价格不能小于0")
        }
        val timestamp = now()
        val product = Product(nextId(), name, optionalText(payload.description) ?: "", price.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString(), timestamp, timestamp)
        products[product.id] = product
        return product
    }

    fun updateProduct(id: String, payload: ProductPayload): Product {
        val current = products[id] ?: throw NotFoundException("商品不存在")
        val name = optionalText(payload.name) ?: current.name
        val description = optionalText(payload.description) ?: current.description
        val price = payload.price?.trim()?.takeIf { it.isNotEmpty() }?.let {
            val amount = requireDecimal(it, "商品价格")
            if (amount < BigDecimal.ZERO) {
                throw BadRequestException("商品价格不能小于0")
            }
            amount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString()
        } ?: current.price

        val updated = current.copy(name = name, description = description, price = price, updatedAt = now())
        products[id] = updated
        return updated
    }

    fun deleteProduct(id: String) {
        if (products.remove(id) == null) {
            throw NotFoundException("商品不存在")
        }
        inventory.entries.removeIf { it.value.productId == id }
        cartItems.entries.removeIf { it.value.productId == id }
        orderItems.entries.removeIf { it.value.productId == id }
    }

    fun listMerchants(page: Int?, size: Int?, keyword: String?, sortBy: String?, sortOrder: String?): PageResponse<Merchant> {
        val pageNumber = normalizePage(page)
        val pageSize = normalizeSize(size)
        val filtered = merchants.values.filter { keyword.isNullOrBlank() || it.name.contains(keyword, ignoreCase = true) }
        val comparator = when (sortBy?.lowercase()) {
            "name" -> compareBy<Merchant> { it.name.lowercase() }
            else -> compareBy<Merchant> { it.createdAt }
        }
        return paginate(applySorting(filtered, sortOrder, comparator), pageNumber, pageSize)
    }

    fun getMerchant(id: String): Merchant = merchants[id] ?: throw NotFoundException("商家不存在")

    fun createMerchant(payload: NamedPayload): Merchant {
        val name = requireText(payload.name, "商家名称")
        val timestamp = now()
        val merchant = Merchant(nextId(), name, timestamp, timestamp)
        merchants[merchant.id] = merchant
        return merchant
    }

    fun updateMerchant(id: String, payload: NamedPayload): Merchant {
        val current = merchants[id] ?: throw NotFoundException("商家不存在")
        val name = requireText(payload.name, "商家名称")
        val updated = current.copy(name = name, updatedAt = now())
        merchants[id] = updated
        return updated
    }

    fun deleteMerchant(id: String) {
        if (merchants.remove(id) == null) {
            throw NotFoundException("商家不存在")
        }
    }

    fun listOrders(page: Int?, size: Int?, userId: String?, sortBy: String?, sortOrder: String?): PageResponse<Order> {
        val pageNumber = normalizePage(page)
        val pageSize = normalizeSize(size)
        val whereClause = if (userId.isNullOrBlank()) "" else "WHERE user_id = ?"
        val sortColumn = when (sortBy?.lowercase()) {
            "totalamount" -> "CAST(total_amount AS DECIMAL(10,2))"
            "userid" -> "user_id"
            else -> "created_at"
        }
        val sortDirection = if (sortOrder.equals("asc", ignoreCase = true)) "ASC" else "DESC"
        val offset = (pageNumber - 1) * pageSize

        val total = if (userId.isNullOrBlank()) {
            jdbcTemplate.queryForObject("SELECT COUNT(*) FROM orders", Long::class.java) ?: 0L
        } else {
            jdbcTemplate.queryForObject("SELECT COUNT(*) FROM orders WHERE user_id = ?", Long::class.java, userId)
                ?: 0L
        }

        val sql = """
            SELECT id, user_id, total_amount, created_at, updated_at
            FROM orders
            $whereClause
            ORDER BY $sortColumn $sortDirection
            LIMIT ? OFFSET ?
        """.trimIndent()

        val args = if (userId.isNullOrBlank()) {
            arrayOf(pageSize, offset)
        } else {
            arrayOf(userId, pageSize, offset)
        }

        val list = jdbcTemplate.query(sql, { rs, _ ->
            Order(
                id = rs.getString("id"),
                userId = rs.getString("user_id"),
                totalAmount = rs.getBigDecimal("total_amount").setScale(2, java.math.RoundingMode.HALF_UP).toPlainString(),
                createdAt = rs.getTimestamp("created_at").toLocalDateTime().format(formatter),
                updatedAt = rs.getTimestamp("updated_at").toLocalDateTime().format(formatter)
            )
        }, *args)

        return PageResponse(total = total, page = pageNumber, size = pageSize, list = list)
    }

    fun getOrder(id: String): OrderDetail {
        val order = jdbcTemplate.queryForObject(
            """
            SELECT id, user_id, total_amount, created_at, updated_at
            FROM orders
            WHERE id = ?
            LIMIT 1
            """.trimIndent(),
            { rs, _ ->
                Order(
                    id = rs.getString("id"),
                    userId = rs.getString("user_id"),
                    totalAmount = rs.getBigDecimal("total_amount").setScale(2, java.math.RoundingMode.HALF_UP).toPlainString(),
                    createdAt = rs.getTimestamp("created_at").toLocalDateTime().format(formatter),
                    updatedAt = rs.getTimestamp("updated_at").toLocalDateTime().format(formatter)
                )
            },
            id
        ) ?: throw NotFoundException("订单不存在")

        val items = jdbcTemplate.query(
            """
            SELECT id, order_id, product_id, quantity, price, created_at, updated_at
            FROM order_items
            WHERE order_id = ?
            ORDER BY created_at, id
            """.trimIndent(),
            { rs, _ ->
                OrderItem(
                    id = rs.getString("id"),
                    orderId = rs.getString("order_id"),
                    productId = rs.getString("product_id"),
                    quantity = rs.getInt("quantity"),
                    price = rs.getBigDecimal("price").setScale(2, java.math.RoundingMode.HALF_UP).toPlainString(),
                    createdAt = rs.getTimestamp("created_at").toLocalDateTime().format(formatter),
                    updatedAt = rs.getTimestamp("updated_at").toLocalDateTime().format(formatter)
                )
            },
            id
        )
        return OrderDetail(
            id = order.id,
            userId = order.userId,
            totalAmount = order.totalAmount,
            createdAt = order.createdAt,
            updatedAt = order.updatedAt,
            items = items
        )
    }

    fun createOrder(payload: OrderPayload): Order {
        val userId = requireText(payload.userId, "用户ID")
        val userExists = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE id = ?", Long::class.java, userId) ?: 0L
        if (userExists <= 0L) {
            throw NotFoundException("用户不存在")
        }
        val totalAmount = requireDecimal(payload.totalAmount, "订单总金额")
        val timestamp = now()
        val orderId = nextId()
        val order = Order(orderId, userId, totalAmount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString(), timestamp, timestamp)
        jdbcTemplate.update(
            "INSERT INTO orders (id, user_id, total_amount, created_at, updated_at) VALUES (?, ?, ?, ?, ?)",
            order.id,
            order.userId,
            order.totalAmount,
            order.createdAt,
            order.updatedAt
        )

        val submittedItems = payload.items.orEmpty().map { item ->
            val productId = requireText(item.productId, "商品ID")
            val product = products[productId] ?: throw NotFoundException("商品不存在")
            val quantity = requirePositiveInt(item.quantity, "数量")
            val price = item.price?.trim()?.takeIf { it.isNotEmpty() } ?: product.price
            val orderItemId = nextId()
            jdbcTemplate.update(
                "INSERT INTO order_items (id, order_id, product_id, quantity, price, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
                orderItemId,
                order.id,
                productId,
                quantity,
                price,
                timestamp,
                timestamp
            )
            item.copy(price = price)
        }

        val merchantIds = submittedItems.mapNotNull { item ->
            item.productId?.let { findMerchantIdByProductId(it) }
        }.distinct()

        orderEventPublisher.publish(
            OrderSubmittedEvent(
                orderId = order.id,
                userId = userId,
                totalAmount = order.totalAmount,
                items = submittedItems,
                merchantIds = merchantIds,
                createdAt = timestamp
            )
        )
        return order
    }

    fun updateOrder(id: String, payload: OrderPayload): Order {
        val current = jdbcTemplate.queryForObject(
            "SELECT id, user_id, total_amount, created_at, updated_at FROM orders WHERE id = ? LIMIT 1",
            { rs, _ ->
                Order(
                    id = rs.getString("id"),
                    userId = rs.getString("user_id"),
                    totalAmount = rs.getBigDecimal("total_amount").setScale(2, java.math.RoundingMode.HALF_UP).toPlainString(),
                    createdAt = rs.getTimestamp("created_at").toLocalDateTime().format(formatter),
                    updatedAt = rs.getTimestamp("updated_at").toLocalDateTime().format(formatter)
                )
            },
            id
        ) ?: throw NotFoundException("订单不存在")
        val totalAmount = payload.totalAmount?.trim()?.takeIf { it.isNotEmpty() }?.let {
            val amount = requireDecimal(it, "订单总金额")
            amount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString()
        } ?: current.totalAmount
        val updated = current.copy(totalAmount = totalAmount, updatedAt = now())
        jdbcTemplate.update(
            "UPDATE orders SET total_amount = ?, updated_at = ? WHERE id = ?",
            updated.totalAmount,
            updated.updatedAt,
            id
        )
        return updated
    }

    fun deleteOrder(id: String) {
        val affected = jdbcTemplate.update("DELETE FROM orders WHERE id = ?", id)
        if (affected <= 0) {
            throw NotFoundException("订单不存在")
        }
        jdbcTemplate.update("DELETE FROM order_items WHERE order_id = ?", id)
        jdbcTemplate.update("DELETE FROM payments WHERE order_id = ?", id)
    }

    fun listCarts(page: Int?, size: Int?, userId: String?): PageResponse<Cart> {
        val pageNumber = normalizePage(page)
        val pageSize = normalizeSize(size)
        val filtered = carts.values.filter { userId.isNullOrBlank() || it.userId == userId }
        return paginate(filtered.sortedByDescending { it.createdAt }, pageNumber, pageSize)
    }

    fun listCartItems(page: Int?, size: Int?, cartId: String?, productId: String?): PageResponse<CartItem> {
        val pageNumber = normalizePage(page)
        val pageSize = normalizeSize(size)
        val filtered = cartItems.values.filter {
            (cartId.isNullOrBlank() || it.cartId == cartId) && (productId.isNullOrBlank() || it.productId == productId)
        }
        return paginate(filtered.sortedByDescending { it.createdAt }, pageNumber, pageSize)
    }

    fun createCartItem(payload: CartItemPayload): CartItem {
        val cartId = requireText(payload.cartId, "购物车ID")
        val productId = requireText(payload.productId, "商品ID")
        val quantity = requirePositiveInt(payload.quantity, "数量")
        if (carts[cartId] == null) {
            throw NotFoundException("购物车不存在")
        }
        if (products[productId] == null) {
            throw NotFoundException("商品不存在")
        }
        val timestamp = now()
        val itemId = nextId()
        val item = CartItem(itemId, cartId, productId, quantity, timestamp, timestamp)
        cartItems[item.id] = item
        return item
    }

    fun updateCartItem(id: String, payload: CartItemPayload): CartItem {
        val current = cartItems[id] ?: throw NotFoundException("购物车项不存在")
        val quantity = requirePositiveInt(payload.quantity, "数量")
        val updated = current.copy(quantity = quantity, updatedAt = now())
        cartItems[id] = updated
        return updated
    }

    fun deleteCartItem(id: String) {
        if (cartItems.remove(id) == null) {
            throw NotFoundException("购物车项不存在")
        }
    }

    fun listCoupons(page: Int?, size: Int?, code: String?, sortBy: String?, sortOrder: String?): PageResponse<Coupon> {
        val pageNumber = normalizePage(page)
        val pageSize = normalizeSize(size)
        val filtered = coupons.values.filter { code.isNullOrBlank() || it.code.contains(code, ignoreCase = true) }
        val comparator = when (sortBy?.lowercase()) {
            "code" -> compareBy<Coupon> { it.code.lowercase() }
            "discountamount" -> compareBy<Coupon> { BigDecimal(it.discountAmount) }
            else -> compareBy<Coupon> { it.createdAt }
        }
        return paginate(applySorting(filtered, sortOrder, comparator), pageNumber, pageSize)
    }

    fun createCoupon(payload: CouponPayload): Coupon {
        val code = requireText(payload.code, "优惠券编码")
        if (coupons.values.any { it.code.equals(code, ignoreCase = true) }) {
            throw ConflictException("优惠券编码已存在")
        }
        val discountAmount = requireDecimal(payload.discountAmount, "优惠金额")
        val timestamp = now()
        val coupon = Coupon(nextId(), code, discountAmount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString(), timestamp, timestamp)
        coupons[coupon.id] = coupon
        return coupon
    }

    fun updateCoupon(id: String, payload: CouponPayload): Coupon {
        val current = coupons[id] ?: throw NotFoundException("优惠券不存在")
        val code = requireText(payload.code, "优惠券编码")
        if (code != current.code && coupons.values.any { it.code.equals(code, ignoreCase = true) }) {
            throw ConflictException("优惠券编码已存在")
        }
        val discountAmount = requireDecimal(payload.discountAmount, "优惠金额")
        val updated = current.copy(
            code = code,
            discountAmount = discountAmount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString(),
            updatedAt = now()
        )
        coupons[id] = updated
        return updated
    }

    fun deleteCoupon(id: String) {
        if (coupons.remove(id) == null) {
            throw NotFoundException("优惠券不存在")
        }
    }

    fun listAddresses(page: Int?, size: Int?, userId: String?, city: String?, state: String?, sortBy: String?, sortOrder: String?): PageResponse<Address> {
        val pageNumber = normalizePage(page)
        val pageSize = normalizeSize(size)
        val filtered = addresses.values.filter {
            (userId.isNullOrBlank() || it.userId == userId) &&
                (city.isNullOrBlank() || it.city.contains(city, ignoreCase = true)) &&
                (state.isNullOrBlank() || it.state.contains(state, ignoreCase = true))
        }
        val comparator = when (sortBy?.lowercase()) {
            "city" -> compareBy<Address> { it.city.lowercase() }
            "state" -> compareBy<Address> { it.state.lowercase() }
            else -> compareBy<Address> { it.createdAt }
        }
        return paginate(applySorting(filtered, sortOrder, comparator), pageNumber, pageSize)
    }

    fun createAddress(payload: AddressPayload): Address {
        val userId = requireText(payload.userId, "用户ID")
        if (users[userId] == null) {
            throw NotFoundException("用户不存在")
        }
        val street = requireText(payload.street, "街道")
        val city = requireText(payload.city, "城市")
        val state = requireText(payload.state, "省份")
        val zipCode = requireText(payload.zipCode, "邮编")
        val country = requireText(payload.country, "国家")
        val timestamp = now()
        val addressId = nextId()
        val address = Address(addressId, userId, street, city, state, zipCode, country, timestamp, timestamp)
        addresses[address.id] = address
        return address
    }

    fun updateAddress(id: String, payload: AddressPayload): Address {
        val current = addresses[id] ?: throw NotFoundException("地址不存在")
        val updated = current.copy(
            street = requireText(payload.street, "街道"),
            city = requireText(payload.city, "城市"),
            state = requireText(payload.state, "省份"),
            zipCode = requireText(payload.zipCode, "邮编"),
            country = requireText(payload.country, "国家"),
            updatedAt = now()
        )
        addresses[id] = updated
        return updated
    }

    fun deleteAddress(id: String) {
        if (addresses.remove(id) == null) {
            throw NotFoundException("地址不存在")
        }
    }

    fun listInventory(page: Int?, size: Int?, productId: String?, sortBy: String?, sortOrder: String?): PageResponse<Inventory> {
        val pageNumber = normalizePage(page)
        val pageSize = normalizeSize(size)
        val filtered = inventory.values.filter { productId.isNullOrBlank() || it.productId == productId }
        val comparator = when (sortBy?.lowercase()) {
            "quantity" -> compareBy<Inventory> { it.quantity }
            else -> compareBy<Inventory> { it.createdAt }
        }
        return paginate(applySorting(filtered, sortOrder, comparator), pageNumber, pageSize)
    }

    fun createInventory(payload: InventoryPayload): Inventory {
        val productId = requireText(payload.productId, "商品ID")
        val quantity = requirePositiveInt(payload.quantity, "库存数量")
        if (products[productId] == null) {
            throw NotFoundException("商品不存在")
        }
        val timestamp = now()
        val inventoryId = nextId()
        val item = Inventory(inventoryId, productId, quantity, timestamp, timestamp)
        inventory[item.id] = item
        return item
    }

    fun updateInventory(id: String, payload: InventoryPayload): Inventory {
        val current = inventory[id] ?: throw NotFoundException("库存不存在")
        val quantity = requirePositiveInt(payload.quantity, "库存数量")
        val updated = current.copy(quantity = quantity, updatedAt = now())
        inventory[id] = updated
        return updated
    }

    fun deleteInventory(id: String) {
        if (inventory.remove(id) == null) {
            throw NotFoundException("库存不存在")
        }
    }

    fun listPayments(page: Int?, size: Int?, orderId: String?, sortBy: String?, sortOrder: String?): PageResponse<Payment> {
        val pageNumber = normalizePage(page)
        val pageSize = normalizeSize(size)
        val filtered = payments.values.filter { orderId.isNullOrBlank() || it.orderId == orderId }
        val comparator = when (sortBy?.lowercase()) {
            "amount" -> compareBy<Payment> { BigDecimal(it.amount) }
            "paymentmethod" -> compareBy<Payment> { it.paymentMethod }
            else -> compareBy<Payment> { it.createdAt }
        }
        return paginate(applySorting(filtered, sortOrder, comparator), pageNumber, pageSize)
    }

    fun createPayment(payload: PaymentPayload): Payment {
        val orderId = requireText(payload.orderId, "订单ID")
        val amount = requireDecimal(payload.amount, "支付金额")
        val paymentMethod = requirePositiveInt(payload.paymentMethod, "支付方式")
        if (paymentMethod !in 1..3) {
            throw BadRequestException("支付方式仅支持 1, 2, 3")
        }
        if (orders[orderId] == null) {
            throw NotFoundException("订单不存在")
        }
        val timestamp = now()
        val paymentId = nextId()
        val payment = Payment(paymentId, orderId, amount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString(), paymentMethod, timestamp, timestamp)
        payments[payment.id] = payment
        return payment
    }

    fun updatePayment(id: String, payload: PaymentPayload): Payment {
        val current = payments[id] ?: throw NotFoundException("支付不存在")
        val amount = requireDecimal(payload.amount, "支付金额")
        val paymentMethod = requirePositiveInt(payload.paymentMethod, "支付方式")
        if (paymentMethod !in 1..3) {
            throw BadRequestException("支付方式仅支持 1, 2, 3")
        }
        val updated = current.copy(
            amount = amount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString(),
            paymentMethod = paymentMethod,
            updatedAt = now()
        )
        payments[id] = updated
        return updated
    }

    fun deletePayment(id: String) {
        if (payments.remove(id) == null) {
            throw NotFoundException("支付不存在")
        }
    }

    fun listLogs(page: Int?, size: Int?, userId: String?, logType: Int?, sortBy: String?, sortOrder: String?): PageResponse<LogRecord> {
        val pageNumber = normalizePage(page)
        val pageSize = normalizeSize(size)
        val filtered = logs.values.filter {
            (userId.isNullOrBlank() || it.userId == userId) && (logType == null || it.logType == logType)
        }
        val comparator = when (sortBy?.lowercase()) {
            "logtype" -> compareBy<LogRecord> { it.logType }
            else -> compareBy<LogRecord> { it.createdAt }
        }
        return paginate(applySorting(filtered, sortOrder, comparator), pageNumber, pageSize)
    }

    fun createLog(payload: LogPayload): LogRecord {
        val userId = requireText(payload.userId, "用户ID")
        val logType = requirePositiveInt(payload.logType, "日志类型")
        if (logType !in 1..2) {
            throw BadRequestException("日志类型仅支持 1 或 2")
        }
        if (users[userId] == null) {
            throw NotFoundException("用户不存在")
        }
        val message = requireText(payload.message, "日志内容")
        val timestamp = now()
        val logId = nextId()
        val log = LogRecord(logId, userId, logType, message, timestamp, timestamp)
        logs[log.id] = log
        return log
    }

    fun updateLog(id: String, payload: LogPayload): LogRecord {
        val current = logs[id] ?: throw NotFoundException("日志不存在")
        val logType = requirePositiveInt(payload.logType, "日志类型")
        if (logType !in 1..2) {
            throw BadRequestException("日志类型仅支持 1 或 2")
        }
        val message = requireText(payload.message, "日志内容")
        val updated = current.copy(logType = logType, message = message, updatedAt = now())
        logs[id] = updated
        return updated
    }

    fun deleteLog(id: String) {
        if (logs.remove(id) == null) {
            throw NotFoundException("日志不存在")
        }
    }

    private fun ensureCart(userId: String): Cart {
        carts.values.firstOrNull { it.userId == userId }?.let { return it }
        val timestamp = now()
        val cartId = nextId()
        val cart = Cart(cartId, userId, timestamp, timestamp)
        carts[cart.id] = cart
        return cart
    }
}
