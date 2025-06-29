package com.motycka.edu.order
import kotlinx.serialization.Serializable
typealias OrderItemId = Long

@Serializable
data class OrderItem(
    val menuItem: MenuItem,
    val quantity: Int
)