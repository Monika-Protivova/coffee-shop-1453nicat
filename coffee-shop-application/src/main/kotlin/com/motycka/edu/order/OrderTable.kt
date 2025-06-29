package com.motycka.edu.order

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column

// Import your enum OrderStatus here:
import com.motycka.edu.order.OrderStatus

object OrderTable : LongIdTable("orders") {
    val customerId: Column<Long> = long("customer_id")
    val status: Column<OrderStatus> = enumerationByName("status", 20, OrderStatus::class)
    val isPaid: Column<Boolean> = bool("is_paid").default(false)
}

class OrderDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<OrderDAO>(OrderTable)

    var customerId by OrderTable.customerId
    var status by OrderTable.status
    var isPaid by OrderTable.isPaid

    // Accept List<OrderItem> (full data), not List<OrderItemDTO>
    fun toDTO(items: List<OrderItem>): OrderDTO {
        val totalPrice = items.sumOf { it.menuItem.price * it.quantity }
        return OrderDTO(
            id = id.value,
            customerId = customerId,
            menuItems = items,
            totalPrice = totalPrice,
            status = status,
            isPaid = isPaid
        )
    }
}