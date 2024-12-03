package ruslan.shastkiv.bookstore.utils;

import static ruslan.shastkiv.bookstore.utils.UserTestUtils.CUSTOM_SHIPPING_ADDRESS;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.test.web.servlet.MvcResult;
import ruslan.shastkiv.bookstore.dto.order.OrderDto;
import ruslan.shastkiv.bookstore.dto.order.OrderItemDto;
import ruslan.shastkiv.bookstore.dto.order.PlaceOrderRequestDto;
import ruslan.shastkiv.bookstore.dto.order.UpdateOrderStatusRequestDto;
import ruslan.shastkiv.bookstore.model.Order;
/*
NOTE!!!
in DB we have 2 test users with id 2 and 3
user with id 3 have 2 orders with id 1 and 2
order with id 1 have 2 order items with id 1 and 2 and books with id 1, 2
order with id 2 have 1 order item with id 3 and book with id 3
 */

public class OrderTestUtils {
    public static final String ORDER_URL = "/orders";
    public static final String ORDER_URL_WITH_ID_1 = "/orders/1";
    public static final String ORDER_ITEMS_URL = "/items";
    public static final String ORDER_ITEMS_URL_WiTH_ID_1 = "/items/1";

    public static final Long ORDER_ID_1 = 1L;
    public static final Long ORDER_ITEM_ID_1 = 1L;
    public static final Long ORDER_ITEM_ID_2 = 2L;

    public static PlaceOrderRequestDto createPlaceOrderRequestDto(Long userId) {
        return new PlaceOrderRequestDto(
                CUSTOM_SHIPPING_ADDRESS.formatted(userId)
        );
    }

    public static OrderItemDto createOrderItemDto(Long id) {
        return new OrderItemDto(
                id,
                id,
                id.intValue()
        );
    }

    public static OrderDto createOrderDto(Long id, List<OrderItemDto> items) {
        return new OrderDto(
                id,
                id,
                items,
                LocalDateTime.now(),
                BigDecimal.valueOf(9L),
                Order.Status.PENDING);
    }

    public static List<OrderDto> getOrderDtosFromMvcResult(
            MvcResult result,
            ObjectMapper objectMapper) throws Exception {
        return objectMapper.convertValue(
                objectMapper.readTree(result.getResponse().getContentAsString())
                        .get("content"), new TypeReference<List<OrderDto>>() {}
        );
    }

    public static List<OrderItemDto> getOrderItemDtosFromMvcResult(
            MvcResult result,
            ObjectMapper objectMapper) throws Exception {
        return objectMapper.convertValue(
                objectMapper.readTree(result.getResponse().getContentAsString())
                        .get("content"), new TypeReference<List<OrderItemDto>>() {}
        );
    }

    public static UpdateOrderStatusRequestDto createUpdateStatusDto(Order.Status status) {
        return new UpdateOrderStatusRequestDto(status);
    }
}
