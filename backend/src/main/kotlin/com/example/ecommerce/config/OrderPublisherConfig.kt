package com.example.ecommerce.config

import com.example.ecommerce.model.OrderSubmittedEvent
import com.example.ecommerce.service.OrderEventPublisher
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class OrderPublisherConfig {
    @Bean
    @ConditionalOnMissingBean(OrderEventPublisher::class)
    open fun noOpOrderEventPublisher(): OrderEventPublisher {
        return object : OrderEventPublisher {
            override fun publish(event: OrderSubmittedEvent) {
                // no-op in local/dev when RocketMQ not available
            }
        }
    }
}
