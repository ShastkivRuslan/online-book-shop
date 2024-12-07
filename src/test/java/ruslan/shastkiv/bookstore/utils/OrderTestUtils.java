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

    /**
     * Creates a {@link PlaceOrderRequestDto} with a formatted shipping address for the specified
     * user ID. The shipping address is generated using a custom address format and the user ID.
     * This method is typically used when creating a request to place an order, where the shipping
     * address depends on the user.
     *
     * @param userId the ID of the user for whom the shipping address is created.
     * @return a {@link PlaceOrderRequestDto} with the generated shipping address.
     */

    public static PlaceOrderRequestDto createPlaceOrderRequestDto(Long userId) {
        return new PlaceOrderRequestDto(
                CUSTOM_SHIPPING_ADDRESS.formatted(userId)
        );
    }

    /**
     * Creates an {@link OrderDto} with the specified order ID, a list of {@link OrderItemDto}s,
     * and the order status. The order date is set to the current time.
     * The total cost is calculated as the sum of the products of the item quantities
     * and their IDs (used as a placeholder for price in this test utility).
     * This method is useful for creating an {@link OrderDto} with
     * predefined values for testing or other purposes.
     *
     * @param id     the ID of the order;
     * @param items  the list of {@link OrderItemDto}s representing the items in the order;
     * @param status the {@link Order.Status} representing the current status of the order;
     * @return an {@link OrderDto} instance with the specified values.
     */

    public static OrderDto createOrderDto(Long id, List<OrderItemDto> items, Order.Status status) {
        return new OrderDto(
                id,
                id,
                items,
                LocalDateTime.now(),
                items.stream()
                        .map(orderItem -> new BigDecimal(orderItem.quantity())
                                .multiply(new BigDecimal(orderItem.id())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add),
                status);
    }

    /**
     * Creates an instance of {@link UpdateOrderStatusRequestDto} with the specified order status.
     *
     * @param status the new status to set for the order;
     * @return an {@link UpdateOrderStatusRequestDto} containing the specified status.
     */

    public static UpdateOrderStatusRequestDto createUpdateStatusDto(Order.Status status) {
        return new UpdateOrderStatusRequestDto(status);
    }

    /**
     * Creates an {@link OrderItem} for the specified book ID and associates it with the given
     * {@link Order}. The item is populated with the corresponding book details (using the provided
     * {@code bookId}) and the quantity is set to the book's ID value.
     *
     * <p>This method should be used when creating an order item that is explicitly associated
     * with an order.
     *
     * @param order  the {@link Order} to associate the order item with;
     * @param bookId the ID of the book for which the order item is created;
     * @return an {@link OrderItem} instance associated with the given order and book.
     */

    public static OrderItem createOrderItem(Long bookId, Order order) {
        OrderItem orderItem = createOrderItem(bookId);
        orderItem.setOrder(order);
        return orderItem;
    }

    /**
     * Creates an {@link OrderItem} for the specified book ID. The item is populated with the
     * corresponding book details (using the provided {@code bookId}) and the quantity is set
     * to the book's ID value. This method is useful for creating order items when the order
     * association is not relevant (for example, in unit tests).
     *
     * <p>This method should be used when you don't need to associate the order item with a
     * specific order (e.g., for testing purposes).
     *
     * @param bookId the ID of the book for which the order item is created;
     * @return an {@link OrderItem} instance with the given book details and quantity.
     */

    public static OrderItem createOrderItem(Long bookId) {
        Book book = createBookById(bookId, List.of(FIRST_CATEGORY_ID));
        OrderItem orderItem = new OrderItem();
        orderItem.setId(bookId);
        orderItem.setBook(book);
        orderItem.setQuantity(book.getId().intValue());
        orderItem.setPrice(BigDecimal.valueOf(bookId.doubleValue()));
        return orderItem;
    }

    /**
     * Creates an {@link OrderItemDto} using the provided book ID. The fields of the created
     * {@link OrderItemDto} are populated as follows:
     * <ul>
     *     <li>{@code id} - set to the provided {@code bookId}.</li>
     *     <li>{@code bookId} - set to the provided {@code bookId}.</li>
     *     <li>{@code quantity} - set to the integer value of the provided {@code bookId}.</li>
     * </ul>
     *
     * @param bookId the ID of the book to create the {@link OrderItemDto} for; must not be
     *               {@code null}.
     * @return an {@link OrderItemDto} with the fields initialized based on the provided
     *         {@code bookId}.
     */

    public static OrderItemDto createOrderItemDto(Long bookId) {
        return new OrderItemDto(
                bookId,
                bookId,
                bookId.intValue()
        );
    }

    /**
     * Creates an {@link OrderDto} for the specified user and populates it with
     * {@link OrderItemDto}s based on the provided list of item IDs.
     * The {@link OrderDto} ID will be the same as the user's ID
     * for simplicity. The total cost is calculated as the sum of the
     * products of the item quantities and their IDs
     * (used as a placeholder for price in this test utility).
     *
     * <p>The created {@link OrderDto} will include:
     * - The same ID for both the order and the user.
     * - A list of {@link OrderItemDto}s created based on the provided item IDs.
     * - A default order date.
     * - The calculated total cost based on the item quantities and IDs.
     * - The specified order status.
     *
     * @param userId  the ID of the user who placed the order;
     * @param itemIds a list of item IDs to be included in the order;
     * @param status  the status of the order.
     * @return an {@link OrderDto} instance with the specified user ID, items, and status.
     */

    public static OrderDto createOrderDtoWithItems(Long userId,
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

    /**
     * Creates an {@link Order} for the specified {@link User} and populates it with
     * {@link OrderItem}s based on the provided list of book IDs.
     * The order is initialized with default values for status,
     * order date, and shipping address. The total cost is calculated
     * as the sum of the prices of all items multiplied by their quantities.
     *
     * <p>The created {@link Order} will have the same ID as the provided user.
     * The created {@link OrderItem} will have the same ID as the associated book.
     *
     * @param user    the {@link User} for whom the order is created;
     * @param bookIds a list of book IDs to be added to the order as items;
     * @return an {@link Order} instance with the specified user and populated items.
     */

    public static Order createOrderWithItems(User user, List<Long> bookIds) {
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
