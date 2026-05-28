package com.example.ecommerce.service

import com.example.ecommerce.model.OrderSubmittedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.rocketmq.spring.core.RocketMQTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class RocketMqOrderEventPublisher(
    private val rocketMQTemplate: RocketMQTemplate,
    private val objectMapper: ObjectMapper,
    @Value("\${ecommerce.rocketmq.order-topic}") private val orderTopic: String
) : OrderEventPublisher {
    override fun publish(event: OrderSubmittedEvent) {
        rocketMQTemplate.convertAndSend(orderTopic, objectMapper.writeValueAsString(event))
    }
}
