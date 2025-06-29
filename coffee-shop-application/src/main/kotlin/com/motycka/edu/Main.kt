package com.motycka.edu

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

fun main(args: Array<String>) {


}

object MenuItemTable : LongIdTable("menu_item") {
    val name = text("name")
    val description = text("description")
    val price = double("price")
    val isDeleted = bool("is_deleted").default(false)
}

object OrderTable : LongIdTable("orders") {
    val customerName = text("customer_name")
    val orderDate = datetime("order_date")
    val totalAmount = double("total_amount")
}

object OrderItemTable : LongIdTable("order_item") {
    val menuItemId = reference("menu_item_id", MenuItemTable.id)
    val orderId = reference("order_id", OrderTable.id)
    val quantity = integer("quantity")
}
