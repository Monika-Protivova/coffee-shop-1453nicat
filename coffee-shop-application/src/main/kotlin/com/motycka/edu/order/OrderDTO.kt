package com.motycka.edu.order

import kotlinx.serialization.Serializable

@Serializable
data class OrderDTO(
    val id: Long,
    val customerId: Long,
    val menuItems: List<OrderItem>,
    val totalPrice: Double,
    val status: OrderStatus,
    val isPaid: Boolean
)