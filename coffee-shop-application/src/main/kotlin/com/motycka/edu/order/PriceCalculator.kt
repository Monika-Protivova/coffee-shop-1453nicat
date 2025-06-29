package com.motycka.edu.order

import com.motycka.edu.menu.MenuItemDTO

object PriceCalculator {
    fun calculatePrice(
        menuItems: List<MenuItemDTO>,
        discountInPercent: Double,
        orderItems: List<OrderItemDTO> = emptyList()
    ): Double {
        val menuItemsById = menuItems.associateBy { it.id }

        val originalPrice = orderItems.sumOf { orderItem ->
            val menuItem = menuItemsById[orderItem.menuItemId]
            if (menuItem != null) {
                menuItem.price * orderItem.quantity
            } else {
                0.0
            }
        }

        // finalPrice = originalPrice * (1 - discountPercent/100)
        val finalPrice = originalPrice * (1 - discountInPercent / 100.0)
        return "%.2f".format(finalPrice).toDouble()
    }
}