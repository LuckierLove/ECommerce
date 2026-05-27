package com.example.ecommerce.service

import com.example.ecommerce.model.OrderSubmittedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.rocketmq.spring.annotation.ConsumeMode
import org.apache.rocketmq.spring.annotation.MessageModel
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener
import org.apache.rocketmq.spring.core.RocketMQListener
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
@RocketMQMessageListener(
    topic = "\${ecommerce.rocketmq.order-topic}",
    consumerGroup = "\${rocketmq.consumer.group}",
    consumeMode = ConsumeMode.CONCURRENTLY,
    messageModel = MessageModel.CLUSTERING
)
class RocketMqMerchantOrderConsumer(
    private val objectMapper: ObjectMapper,
    private val merchantNotificationService: MerchantNotificationService
) : RocketMQListener<String> {
    override fun onMessage(message: String) {
        val event = objectMapper.readValue(message, OrderSubmittedEvent::class.java)
        event.merchantIds.forEach { merchantId ->
            merchantNotificationService.addOrderNotice(
                merchantId = merchantId,
                orderId = event.orderId,
                userId = event.userId,
                totalAmount = event.totalAmount,
                itemCount = event.items.sumOf { it.quantity ?: 0 }
            )
        }
    }
}
