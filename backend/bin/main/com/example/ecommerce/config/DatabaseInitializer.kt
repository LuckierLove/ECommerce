package com.example.ecommerce.config

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class DatabaseInitializer(
    private val jdbcTemplate: JdbcTemplate
    , private val dataSource: DataSource
) : ApplicationRunner {
    override fun run(args: ApplicationArguments) {
        createTables()
        loadSeedData()
    }

    private fun createTables() {
        val ddlStatements = listOf(
            """
            CREATE TABLE IF NOT EXISTS users (
                id VARCHAR(255) PRIMARY KEY,
                username VARCHAR(255) NOT NULL UNIQUE,
                email VARCHAR(255) NOT NULL UNIQUE,
                password VARCHAR(255) NOT NULL,
                created_at TIMESTAMP NOT NULL,
                updated_at TIMESTAMP NOT NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS roles (
                id VARCHAR(255) PRIMARY KEY,
                name VARCHAR(255) NOT NULL UNIQUE,
                created_at TIMESTAMP NOT NULL,
                updated_at TIMESTAMP NOT NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS user_roles (
                user_id VARCHAR(255) NOT NULL,
                role_id VARCHAR(255) NOT NULL,
                created_at TIMESTAMP NOT NULL,
                updated_at TIMESTAMP NOT NULL,
                PRIMARY KEY (user_id, role_id)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS permissions (
                id VARCHAR(255) PRIMARY KEY,
                name VARCHAR(255) NOT NULL UNIQUE,
                created_at TIMESTAMP NOT NULL,
                updated_at TIMESTAMP NOT NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS role_permissions (
                role_id VARCHAR(255) NOT NULL,
                permission_id VARCHAR(255) NOT NULL,
                created_at TIMESTAMP NOT NULL,
                updated_at TIMESTAMP NOT NULL,
                PRIMARY KEY (role_id, permission_id)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS products (
                id VARCHAR(255) PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                description TEXT,
                price DECIMAL(10,2) NOT NULL,
                created_at TIMESTAMP NOT NULL,
                updated_at TIMESTAMP NOT NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS merchants (
                id VARCHAR(255) PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                created_at TIMESTAMP NOT NULL,
                updated_at TIMESTAMP NOT NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS merchant_users (
                merchant_id VARCHAR(255) NOT NULL,
                user_id VARCHAR(255) NOT NULL,
                created_at TIMESTAMP NOT NULL,
                updated_at TIMESTAMP NOT NULL,
                PRIMARY KEY (merchant_id, user_id)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS merchant_products (
                merchant_id VARCHAR(255) NOT NULL,
                product_id VARCHAR(255) NOT NULL,
                quantity INT NOT NULL,
                created_at TIMESTAMP NOT NULL,
                updated_at TIMESTAMP NOT NULL,
                PRIMARY KEY (merchant_id, product_id)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS orders (
                id VARCHAR(255) PRIMARY KEY,
                user_id VARCHAR(255) NOT NULL,
                total_amount DECIMAL(10,2) NOT NULL,
                subtotal DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                tax_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                shipping_cost DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                currency VARCHAR(10) NOT NULL DEFAULT 'CNY',
                status VARCHAR(50) NOT NULL DEFAULT 'CREATED',
                payment_status VARCHAR(50) NOT NULL DEFAULT 'UNPAID',
                items_count INT NOT NULL DEFAULT 0,
                billing_address_id VARCHAR(255),
                shipping_address_id VARCHAR(255),
                shipping_method VARCHAR(255),
                tracking_number VARCHAR(255),
                note TEXT,
                created_at TIMESTAMP NOT NULL,
                updated_at TIMESTAMP NOT NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS order_items (
                id VARCHAR(255) PRIMARY KEY,
                order_id VARCHAR(255) NOT NULL,
                product_id VARCHAR(255) NOT NULL,
                quantity INT NOT NULL,
                price DECIMAL(10,2) NOT NULL,
                sku VARCHAR(255),
                name VARCHAR(255),
                unit_price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                tax_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                subtotal DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                created_at TIMESTAMP NOT NULL,
                updated_at TIMESTAMP NOT NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS carts (
                id VARCHAR(255) PRIMARY KEY,
                user_id VARCHAR(255) NOT NULL,
                created_at TIMESTAMP NOT NULL,
                updated_at TIMESTAMP NOT NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS cart_items (
                id VARCHAR(255) PRIMARY KEY,
                cart_id VARCHAR(255) NOT NULL,
                product_id VARCHAR(255) NOT NULL,
                quantity INT NOT NULL,
                unit_price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                selected_options TEXT,
                subtotal DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                created_at TIMESTAMP NOT NULL,
                updated_at TIMESTAMP NOT NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS payments (
                id VARCHAR(255) PRIMARY KEY,
                order_id VARCHAR(255) NOT NULL,
                amount DECIMAL(10,2) NOT NULL,
                payment_method INT NOT NULL,
                created_at TIMESTAMP NOT NULL,
                updated_at TIMESTAMP NOT NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS addresses (
                id VARCHAR(255) PRIMARY KEY,
                user_id VARCHAR(255) NOT NULL,
                street VARCHAR(255) NOT NULL,
                city VARCHAR(255) NOT NULL,
                state VARCHAR(255) NOT NULL,
                zip_code VARCHAR(20) NOT NULL,
                country VARCHAR(255) NOT NULL,
                created_at TIMESTAMP NOT NULL,
                updated_at TIMESTAMP NOT NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS inventory (
                id VARCHAR(255) PRIMARY KEY,
                product_id VARCHAR(255) NOT NULL,
                quantity INT NOT NULL,
                created_at TIMESTAMP NOT NULL,
                updated_at TIMESTAMP NOT NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS coupons (
                id VARCHAR(255) PRIMARY KEY,
                code VARCHAR(255) NOT NULL UNIQUE,
                discount_amount DECIMAL(10,2) NOT NULL,
                created_at TIMESTAMP NOT NULL,
                updated_at TIMESTAMP NOT NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS order_coupons (
                order_id VARCHAR(255) NOT NULL,
                coupon_id VARCHAR(255) NOT NULL,
                created_at TIMESTAMP NOT NULL,
                updated_at TIMESTAMP NOT NULL,
                PRIMARY KEY (order_id, coupon_id)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS logs (
                id VARCHAR(255) PRIMARY KEY,
                user_id VARCHAR(255) NOT NULL,
                log_type INT NOT NULL,
                message TEXT NOT NULL,
                created_at TIMESTAMP NOT NULL,
                updated_at TIMESTAMP NOT NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS merchant_notifications (
                id VARCHAR(255) PRIMARY KEY,
                merchant_id VARCHAR(255) NOT NULL,
                order_id VARCHAR(255) NOT NULL,
                user_id VARCHAR(255) NOT NULL,
                message TEXT NOT NULL,
                created_at TIMESTAMP NOT NULL
            )
            """
        )

        ddlStatements.forEach { statement ->
            jdbcTemplate.execute(statement)
        }
    }

    private fun loadSeedData() {
        val classpathResource = ClassPathResource("data.sql")
        val seedResource = if (classpathResource.exists()) {
            classpathResource
        } else {
            FileSystemResource("src/main/resources/data.sql")
        }

        if (!seedResource.exists()) {
            return
        }

        ResourceDatabasePopulator(seedResource).execute(dataSource)

        dataSource.connection.use { connection ->
            connection.autoCommit = false
            connection.prepareStatement(
                "INSERT IGNORE INTO merchant_notifications (id, merchant_id, order_id, user_id, message, created_at) VALUES (?, ?, ?, ?, ?, ?)"
            ).use { statement ->
                listOf(
                    arrayOf("notice-1", "1014", "1031", "1010", "有新的订单提交，订单号 1031，金额 5278.90，商品数量 3", "2026-05-27 10:00:00"),
                    arrayOf("notice-2", "1014", "1031", "1010", "有新的订单提交，订单号 1031，金额 5278.90，商品数量 3", "2026-05-27 10:00:00")
                ).forEach { row ->
                    statement.setString(1, row[0] as String)
                    statement.setString(2, row[1] as String)
                    statement.setString(3, row[2] as String)
                    statement.setString(4, row[3] as String)
                    statement.setString(5, row[4] as String)
                    statement.setString(6, row[5] as String)
                    statement.addBatch()
                }
                statement.executeBatch()
            }
            connection.commit()
        }

        val finalNoticeCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM merchant_notifications", Long::class.java) ?: 0L
        println("Seed data loaded. merchant_notifications=$finalNoticeCount")
    }
}
