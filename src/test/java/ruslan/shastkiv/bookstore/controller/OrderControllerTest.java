package ruslan.shastkiv.bookstore.controller;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.ORDER_ITEMS_URL;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.ORDER_ITEMS_URL_WiTH_ID_1;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.ORDER_ITEM_ID_1;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.ORDER_ITEM_ID_2;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.ORDER_URL;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.ORDER_URL_WITH_ID_1;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.createOrderDto;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.createOrderItemDto;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.createPlaceOrderRequestDto;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.createUpdateStatusDto;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.getOrderDtosFromMvcResult;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.getOrderItemDtosFromMvcResult;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.USER_ID;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.createUser;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.getAuthentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
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
import ruslan.shastkiv.bookstore.model.User;

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
    @WithMockUser(username = "user")
    @Sql(
            scripts = {
                    "classpath:scripts/order/remove_order_items_after_placing_order.sql",
                    "classpath:scripts/order/remove_order_after_placing_order.sql",
                    "classpath:scripts/cart/insert_cart_items.sql"
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void placeOrder_validRequestDto_returnOrderDto() throws Exception {
        User user = createUser(USER_ID);
        Authentication authentication = getAuthentication(user);
        PlaceOrderRequestDto requestDto = createPlaceOrderRequestDto(USER_ID);
        String json = objectMapper.writeValueAsString(requestDto);
        OrderDto expectedDto = createOrderDto(USER_ID, List.of(
                createOrderItemDto(USER_ID)), Order.Status.PENDING);

        MvcResult result = mockMvc.perform(post(ORDER_URL)
                        .with(authentication(authentication))
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
        assertTrue((actualDto.orderDate().getSecond() - expectedDto.orderDate().getSecond()) <= 2);
        assertTrue(reflectionEquals(actualDto, expectedDto, "id", "orderDate", "orderItems"));
    }

    @Test
    @DisplayName("""
            getOrders()
            - Should retrieve all orders by authenticated user
            """)
    @WithMockUser(username = "user")
    public void getOrders_validUser_returnPageOrderDto() throws Exception {
        User user = createUser(USER_ID);
        Authentication authentication = getAuthentication(user);
        List<OrderDto> expectedDtos = List.of(
                createOrderDto(USER_ID, List.of(
                        createOrderItemDto(ORDER_ITEM_ID_1),
                        createOrderItemDto(ORDER_ITEM_ID_2)
                ), Order.Status.PENDING));

        MvcResult result = mockMvc.perform(
                        get(ORDER_URL)
                                .with(authentication(authentication))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<OrderDto> actualDtos = getOrderDtosFromMvcResult(result, objectMapper);
        assertTrue(expectedDtos.size() == actualDtos.size());

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
        assertTrue(actualDto.status().equals(Order.Status.PROCESSING));
    }

    @Test
    @DisplayName("""
            getOrderItems()
            - Should return page with OrderItemDto`s
            """)
    @WithMockUser(username = "user")
    public void getOrderItems_validOrderId_returnPageWithOrderItemDto() throws Exception {
        List<OrderItemDto> expectedDtos = List.of(
                createOrderItemDto(ORDER_ITEM_ID_1),
                createOrderItemDto(ORDER_ITEM_ID_2)
        );

        MvcResult result = mockMvc.perform(
                        get(ORDER_URL_WITH_ID_1 + ORDER_ITEMS_URL)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<OrderItemDto> actualDtos = getOrderItemDtosFromMvcResult(result, objectMapper);

        assertTrue(expectedDtos.stream()
                .allMatch(expectedDto -> actualDtos.stream()
                        .anyMatch(actualDto -> reflectionEquals(expectedDto, actualDto))));
    }

    @Test
    @DisplayName("""
            getOrderItem()
            - Should return valid OrderItemDto
            """)
    @WithMockUser(username = "user")
    public void getOrderItem_validIds_returnOrderItemDto() throws Exception {
        OrderItemDto expectedDto = createOrderItemDto(ORDER_ITEM_ID_1);

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
