package com.motycka.edu.order

import kotlinx.serialization.Serializable

// Data class representing a Menu Item (basic info)
@Serializable
data class MenuItem(
    val id: Long,
    val name: String,
    val description: String,
    val price: Double
)

// Type alias for clarity
typealias OrderId = Long

// Enum for order status with state transition rules
enum class OrderStatus {
    PENDING,
    PAID,
    COMPLETED,
    CANCELLED;

    fun canTransitionTo(newStatus: OrderStatus): Boolean {
        return when (this) {
            PENDING -> newStatus == PAID || newStatus == CANCELLED
            PAID -> newStatus == COMPLETED
            COMPLETED, CANCELLED -> false
        }
    }
}

@Serializable
data class OrderCreateItem(
    val menuItemId: Long,
    val quantity: Int
)
