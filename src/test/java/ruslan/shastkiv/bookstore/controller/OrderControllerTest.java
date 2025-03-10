package ruslan.shastkiv.bookstore.controller;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.FIRST_BOOK_ID;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.SECOND_BOOK_ID;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.THIRD_BOOK_ID;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.ORDER_ITEMS_URL;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.ORDER_ITEMS_URL_WiTH_ID_1;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.ORDER_URL;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.ORDER_URL_WITH_ID_1;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.createOrderDto;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.createOrderItemDto;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.createPlaceOrderRequestDto;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.createUpdateStatusDto;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.getOrderDtosFromMvcResult;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.getOrderItemDtosFromMvcResult;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.USER_ID;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ruslan.shastkiv.bookstore.dto.order.OrderDto;
import ruslan.shastkiv.bookstore.dto.order.OrderItemDto;
import ruslan.shastkiv.bookstore.dto.order.PlaceOrderRequestDto;
import ruslan.shastkiv.bookstore.dto.order.UpdateOrderStatusRequestDto;
import ruslan.shastkiv.bookstore.model.Order;

@Sql(
        scripts = {
                "classpath:scripts/user/insert_user_to_db.sql",
                "classpath:scripts/user/insert_users_roles.sql",
                "classpath:scripts/cart/insert_shopping_cart.sql",
                "classpath:scripts/book/insert_books_to_db.sql",
                "classpath:scripts/cart/insert_cart_items.sql",
                "classpath:scripts/order/insert_order.sql",
                "classpath:scripts/order/insert_order_items.sql"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
@Sql(
        scripts = {
                "classpath:scripts/order/remove_order_items.sql",
                "classpath:scripts/order/remove_order.sql",
                "classpath:scripts/cart/remove_cart_items.sql",
                "classpath:scripts/book/remove_test_books_from_db.sql",
                "classpath:scripts/cart/remove_shopping_cart.sql",
                "classpath:scripts/user/remove_users_roles.sql",
                "classpath:scripts/user/remove_users.sql"

        },
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS
)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerTest {
    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("""
            placeOrder()
            - Should place an order and return Order Dto
            """)
    @WithUserDetails("user_email_3@mail.com")
    @Sql(
            scripts = {
                    "classpath:scripts/order/remove_order_items_after_placing_order.sql",
                    "classpath:scripts/order/remove_order_after_placing_order.sql",
                    "classpath:scripts/cart/insert_cart_items.sql"
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void placeOrder_validRequestDto_returnOrderDto() throws Exception {
        PlaceOrderRequestDto requestDto = createPlaceOrderRequestDto(USER_ID);
        String json = objectMapper.writeValueAsString(requestDto);
        OrderDto expectedDto = createOrderDto(
                USER_ID, List.of(THIRD_BOOK_ID), Order.Status.PENDING);

        MvcResult result = mockMvc.perform(post(ORDER_URL)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        OrderDto actualDto
                = objectMapper.readValue(result.getResponse().getContentAsString(), OrderDto.class);

        assertTrue(expectedDto.orderItems().stream()
                .allMatch(expectedItemDto -> actualDto.orderItems().stream()
                        .anyMatch(actualItemDto
                                -> reflectionEquals(
                                        expectedItemDto, actualItemDto, "id", "orderDate"))));
        assertTrue((actualDto.orderDate().getSecond() - LocalDateTime.now().getSecond() <= 2));
        assertEquals(USER_ID, actualDto.userId());
        assertEquals(Order.Status.PENDING, actualDto.status());
        assertEquals(0, actualDto.total().compareTo(expectedDto.total()));
    }

    @Test
    @DisplayName("""
            getOrders()
            - Should retrieve all orders by authenticated user
            """)
    @WithUserDetails("user_email_3@mail.com")
    public void getOrders_validUser_returnPageOrderDto() throws Exception {
        List<OrderDto> expectedDtos = List.of(
                createOrderDto(USER_ID, List.of(
                        FIRST_BOOK_ID,
                        SECOND_BOOK_ID
                ), Order.Status.PENDING));

        MvcResult result = mockMvc.perform(
                        get(ORDER_URL)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<OrderDto> actualDtos = getOrderDtosFromMvcResult(result, objectMapper);
        assertEquals(expectedDtos.size(), actualDtos.size());
    }

    @Test
    @DisplayName("""
            updateOrderStatus()
            - Should update order status by its id and return Order Dto
            """)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void updateOrderStatus_validRequestDto_returnUpdatedOrderDto() throws Exception {
        UpdateOrderStatusRequestDto requestDto = createUpdateStatusDto(Order.Status.PROCESSING);
        String json = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        patch(ORDER_URL_WITH_ID_1)
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        OrderDto actualDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), OrderDto.class
        );
        assertEquals(Order.Status.PROCESSING, actualDto.status());
    }

    @Test
    @DisplayName("""
            getOrderItems()
            - Should return page with OrderItemDto`s
            """)
    @WithUserDetails("user_email_3@mail.com")
    public void getOrderItems_validOrderId_returnPageWithOrderItemDto() throws Exception {
        List<OrderItemDto> expectedDtos = List.of(
                createOrderItemDto(FIRST_BOOK_ID),
                createOrderItemDto(SECOND_BOOK_ID)
        );

        MvcResult result = mockMvc.perform(
                        get(ORDER_URL_WITH_ID_1 + ORDER_ITEMS_URL)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<OrderItemDto> actualDtos = getOrderItemDtosFromMvcResult(result, objectMapper);

        assertAll("DTO lists comparison",
                () -> assertIterableEquals(
                        expectedDtos.stream().map(OrderItemDto::id).toList(),
                        actualDtos.stream().map(OrderItemDto::id).toList(),
                        "IDs do not match"
                ),
                () -> assertIterableEquals(
                        expectedDtos.stream().map(OrderItemDto::bookId).toList(),
                        actualDtos.stream().map(OrderItemDto::bookId).toList(),
                        "Book IDs do not match"
                ),
                () -> assertIterableEquals(
                        expectedDtos.stream().map(OrderItemDto::quantity).toList(),
                        actualDtos.stream().map(OrderItemDto::quantity).toList(),
                        "Quantities do not match"
                )
        );
    }

    @Test
    @DisplayName("""
            getOrderItem()
            - Should return valid OrderItemDto
            """)
    @WithUserDetails("user_email_3@mail.com")
    public void getOrderItem_validIds_returnOrderItemDto() throws Exception {
        OrderItemDto expectedDto = createOrderItemDto(FIRST_BOOK_ID);

        MvcResult result = mockMvc.perform(
                        get(ORDER_URL_WITH_ID_1 + ORDER_ITEMS_URL_WiTH_ID_1)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        OrderItemDto actualDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), OrderItemDto.class
        );

        assertEquals(expectedDto, actualDto);
    }
}
