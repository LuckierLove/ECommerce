package com.example.ecommerce.service

import com.example.ecommerce.model.OrderSubmittedEvent

interface OrderEventPublisher {
    fun publish(event: OrderSubmittedEvent)
}
