package ruslan.shastkiv.bookstore.utils;

import static ruslan.shastkiv.bookstore.utils.BookTestUtils.createBookById;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.FIRST_CATEGORY_ID;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.CUSTOM_SHIPPING_ADDRESS;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.test.web.servlet.MvcResult;
import ruslan.shastkiv.bookstore.dto.order.OrderDto;
import ruslan.shastkiv.bookstore.dto.order.OrderItemDto;
import ruslan.shastkiv.bookstore.dto.order.PlaceOrderRequestDto;
import ruslan.shastkiv.bookstore.dto.order.UpdateOrderStatusRequestDto;
import ruslan.shastkiv.bookstore.model.Book;
import ruslan.shastkiv.bookstore.model.Order;
import ruslan.shastkiv.bookstore.model.OrderItem;
import ruslan.shastkiv.bookstore.model.User;

public class OrderTestUtils {
    public static final String ORDER_URL = "/orders";
    public static final String ORDER_URL_WITH_ID_1 = "/orders/1";
    public static final String ORDER_ITEMS_URL = "/items";
    public static final String ORDER_ITEMS_URL_WiTH_ID_1 = "/items/1";

    public static final Long ORDER_ID_1 = 1L;
    public static final Long NOT_EXISTED_ORDER_ID = 100L;

    public static final Long ORDER_ITEM_ID_1 = 1L;
    public static final Long NOT_EXISTED_ORDER_ITEM_ID = 100L;

    public static final int EXPECTED_PRICE = 14;

    public static final LocalDateTime ORDER_DATE
            = LocalDateTime.of(2024, 12, 6, 15, 30, 0, 0);

    public static PlaceOrderRequestDto createPlaceOrderRequestDto(Long userId) {
        return new PlaceOrderRequestDto(
                CUSTOM_SHIPPING_ADDRESS.formatted(userId)
        );
    }

    public static UpdateOrderStatusRequestDto createUpdateStatusDto(Order.Status status) {
        return new UpdateOrderStatusRequestDto(status);
    }

    public static OrderItem createOrderItem(Long bookId, Order order) {
        OrderItem orderItem = createOrderItem(bookId);
        orderItem.setOrder(order);
        return orderItem;
    }

    public static OrderItem createOrderItem(Long bookId) {
        Book book = createBookById(bookId, List.of(FIRST_CATEGORY_ID));
        OrderItem orderItem = new OrderItem();
        orderItem.setId(bookId);
        orderItem.setBook(book);
        orderItem.setQuantity(book.getId().intValue());
        orderItem.setPrice(BigDecimal.valueOf(bookId.doubleValue()));
        return orderItem;
    }

    public static OrderItemDto createOrderItemDto(Long bookId) {
        return new OrderItemDto(
                bookId,
                bookId,
                bookId.intValue()
        );
    }

    public static OrderDto createOrderDto(Long userId,
                                          List<Long> itemIds,
                                          Order.Status status) {
        List<OrderItemDto> itemDtos = itemIds.stream()
                .map(OrderTestUtils::createOrderItemDto)
                .toList();
        return new OrderDto(userId,
                userId,
                itemDtos,
                ORDER_DATE,
                BigDecimal.valueOf(itemDtos.stream()
                        .mapToDouble((orderItem -> orderItem.id() * orderItem.id()))
                        .sum()),
                status);
    }

    public static Order createOrder(User user, List<Long> bookIds) {
        Order order = new Order();
        order.setId(user.getId());
        order.setUser(user);
        order.setStatus(Order.Status.PENDING);
        order.setOrderDate(ORDER_DATE);
        order.setShippingAddress(CUSTOM_SHIPPING_ADDRESS.formatted(user.getId()));
        order.setOrderItems(
                bookIds.stream()
                        .map(bookId -> createOrderItem(bookId, order))
                        .collect(Collectors.toSet()));
        order.setTotal(
                order.getOrderItems().stream()
                        .map(orderItem -> orderItem.getPrice()
                                .multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
        return order;
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
}
