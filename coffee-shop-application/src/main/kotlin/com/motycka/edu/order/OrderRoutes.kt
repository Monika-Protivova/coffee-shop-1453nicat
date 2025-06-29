package com.motycka.edu.order

import com.motycka.edu.error.BadRequestException
import com.motycka.edu.error.NotFoundException
import com.motycka.edu.menu.MenuService
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction

private val logger = KotlinLogging.logger {}

private const val ORDER_NOT_FOUND = "Order not found"
private const val INVALID_ID = "Invalid ID format"

fun Route.orderRoutes(
    basePath: String,
    menuService: MenuService // Inject your MenuService here
) {

    fun fetchOrderItems(orderId: Long): List<OrderItemDTO> = transaction {
        OrderItemDAO.find { OrderItemTable.orderId eq orderId }.map { it.toDTO() }
    }

    // Convert MenuItemResponse to your OrderItem.MenuItem data class
    fun mapMenuItemResponseToMenuItem(response: com.motycka.edu.menu.MenuItemResponse): MenuItem {
        return MenuItem(
            id = response.id,
            name = response.name,
            description = response.description,
            price = response.price
        )
    }

    suspend fun mapDTOtoOrderItems(dtos: List<OrderItemDTO>): List<OrderItem> {
        return dtos.mapNotNull { dto ->
            val menuItemResponse = menuService.getMenuItem(dto.menuItemId)
            if (menuItemResponse == null) {
                logger.warn { "MenuItem with id ${dto.menuItemId} not found, skipping this item." }
                null
            } else {
                val menuItem = mapMenuItemResponseToMenuItem(menuItemResponse)
                OrderItem(menuItem = menuItem, quantity = dto.quantity)
            }
        }
    }

    route("$basePath/orders") {

        get {
            val orders = transaction {
                OrderDAO.all().map { orderDAO ->
                    val dtos = fetchOrderItems(orderDAO.id.value)
                    orderDAO to dtos
                }
            }

            val ordersWithFullItems = orders.map { (orderDAO, dtos) ->
                val fullItems = mapDTOtoOrderItems(dtos)
                orderDAO.toDTO(fullItems)
            }

            call.respond(HttpStatusCode.OK, ordersWithFullItems)
        }

        get("{id}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: throw BadRequestException(INVALID_ID)

            val (orderDAO, dtos) = transaction {
                val orderDAO = OrderDAO.findById(id) ?: throw NotFoundException(ORDER_NOT_FOUND)
                val dtos = fetchOrderItems(orderDAO.id.value)
                orderDAO to dtos
            }

            val fullItems = mapDTOtoOrderItems(dtos)
            val orderDTO = orderDAO.toDTO(fullItems)
            call.respond(HttpStatusCode.OK, orderDTO)
        }

        post {
            val request = call.receive<OrderCreateRequest>()

            val orderDTO = transaction {
                val order = OrderDAO.new {
                    customerId = request.customerId
                    status = OrderStatus.PENDING
                    isPaid = false
                }

                request.items.forEach { itemReq ->
                    OrderItemDAO.new {
                        orderId = order.id.value
                        menuItemId = itemReq.menuItemId
                        quantity = itemReq.quantity
                    }
                }

                val dtos = fetchOrderItems(order.id.value)
                order to dtos
            }

            val fullItems = mapDTOtoOrderItems(orderDTO.second)
            call.respond(HttpStatusCode.Created, orderDTO.first.toDTO(fullItems))
        }

        put("{id}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: throw BadRequestException(INVALID_ID)
            val request = call.receive<OrderUpdateRequest>()

            val orderDTO = transaction {
                val order = OrderDAO.findById(id) ?: throw NotFoundException(ORDER_NOT_FOUND)
                order.status = request.status
                val dtos = fetchOrderItems(order.id.value)
                order to dtos
            }

            val fullItems = mapDTOtoOrderItems(orderDTO.second)
            call.respond(HttpStatusCode.OK, orderDTO.first.toDTO(fullItems))
        }
    }
}