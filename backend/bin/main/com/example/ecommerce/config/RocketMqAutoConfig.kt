package com.example.ecommerce.config

import com.example.ecommerce.service.OrderEventPublisher
import com.example.ecommerce.service.RocketMqOrderEventPublisher
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.rocketmq.spring.core.RocketMQTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(RocketMQTemplate::class)
open class RocketMqAutoConfig {
    @Bean
    open fun rocketMqOrderEventPublisher(
        rocketMQTemplate: RocketMQTemplate,
        objectMapper: ObjectMapper,
        @Value("\${ecommerce.rocketmq.order-topic}") orderTopic: String
    ): OrderEventPublisher {
        return RocketMqOrderEventPublisher(rocketMQTemplate, objectMapper, orderTopic)
    }
}
