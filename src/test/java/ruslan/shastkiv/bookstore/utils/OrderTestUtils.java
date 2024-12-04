package ruslan.shastkiv.bookstore.utils;

import static ruslan.shastkiv.bookstore.utils.BookTestUtils.createBookById;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.FIRST_CATEGORY_ID;
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
import ruslan.shastkiv.bookstore.model.Book;
import ruslan.shastkiv.bookstore.model.Order;
import ruslan.shastkiv.bookstore.model.OrderItem;
import ruslan.shastkiv.bookstore.model.User;
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
    public static final Long NOT_EXISTED_ORDER_ID = 100L;
    public static final Long ORDER_ITEM_ID_1 = 1L;
    public static final Long ORDER_ITEM_ID_2 = 2L;
    public static final Long NOT_EXISTED_ORDER_ITEM_ID = 100L;

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

    public static OrderDto createOrderDto(Long id, List<OrderItemDto> items, Order.Status status) {
        return new OrderDto(
                id,
                id,
                items,
                LocalDateTime.now(),
                BigDecimal.valueOf(9L),
                status);
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

    public static Order createOrder(User user, List<OrderItem> orderItems) {
        Order order = new Order();
        order.setId(user.getId());
        order.setUser(user);
        order.setStatus(Order.Status.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(CUSTOM_SHIPPING_ADDRESS.formatted(user.getId()));
        order.setTotal(
                orderItems.stream()
                        .map(orderItem -> orderItem.getPrice()
                                .multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
        return order;
    }

    public static OrderItem createOrderItem(Order order, Long bookId) {
        Book book = createBookById(bookId, List.of(FIRST_CATEGORY_ID));
        OrderItem orderItem = new OrderItem();
        orderItem.setId(order.getId());
        orderItem.setBook(book);
        orderItem.setQuantity(book.getId().intValue());
        orderItem.setPrice(book.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        return orderItem;

    }
}
