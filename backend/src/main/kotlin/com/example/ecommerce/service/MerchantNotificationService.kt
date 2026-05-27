package com.example.ecommerce.service

import com.example.ecommerce.common.PageResponse
import com.example.ecommerce.common.normalizePage
import com.example.ecommerce.common.normalizeSize
import com.example.ecommerce.common.paginate
import com.example.ecommerce.model.MerchantOrderNotice
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import java.sql.PreparedStatement
import java.util.concurrent.atomic.AtomicLong

@Service
class MerchantNotificationService(
    private val jdbcTemplate: JdbcTemplate
) {
    private val idSequence = AtomicLong(5000)

    fun addOrderNotice(merchantId: String, orderId: String, userId: String, totalAmount: String, itemCount: Int) {
        val timestamp = currentTimestamp()
        val notice = MerchantOrderNotice(
            id = idSequence.incrementAndGet().toString(),
            merchantId = merchantId,
            orderId = orderId,
            userId = userId,
            message = "有新的订单提交，订单号 $orderId，金额 $totalAmount，商品数量 $itemCount",
            createdAt = timestamp
        )
        jdbcTemplate.update(
            "INSERT INTO merchant_notifications (id, merchant_id, order_id, user_id, message, created_at) VALUES (?, ?, ?, ?, ?, ?)",
            notice.id,
            notice.merchantId,
            notice.orderId,
            notice.userId,
            notice.message,
            timestamp
        )
    }

    fun listNotices(merchantId: String, page: Int?, size: Int?): PageResponse<MerchantOrderNotice> {
        val pageNumber = normalizePage(page)
        val pageSize = normalizeSize(size)
        val notices = jdbcTemplate.query(
            { connection ->
                val statement: PreparedStatement = connection.prepareStatement(
                    "SELECT id, merchant_id, order_id, user_id, message, created_at FROM merchant_notifications WHERE merchant_id = ? ORDER BY created_at DESC"
                )
                statement.setString(1, merchantId)
                statement
            }
        ) { rs, _ ->
            MerchantOrderNotice(
                id = rs.getString("id"),
                merchantId = rs.getString("merchant_id"),
                orderId = rs.getString("order_id"),
                userId = rs.getString("user_id"),
                message = rs.getString("message"),
                createdAt = rs.getString("created_at")
            )
        }
        return paginate(notices, pageNumber, pageSize)
    }

    private fun currentTimestamp(): String {
        return jdbcTemplate.queryForObject("SELECT DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s')", String::class.java)!!
    }
}
