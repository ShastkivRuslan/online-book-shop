package ruslan.shastkiv.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.FIRST_BOOK_ID;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.PAGEABLE;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.PAGE_SIZE_1;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.SECOND_BOOK_ID;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.THIRD_BOOK_ID;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.ONE_INVOCATION;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.EXPECTED_PRICE;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.ORDER_ID_1;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.ORDER_ITEM_ID_1;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.createOrder;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.createOrderDto;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.createOrderItem;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.createOrderItemDto;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.createPlaceOrderRequestDto;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.createUpdateStatusDto;
import static ruslan.shastkiv.bookstore.utils.ShoppingCartTestUtils.createCartWithItems;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.USER_ID;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.createUser;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.getAuthentication;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.Authentication;
import ruslan.shastkiv.bookstore.dto.order.OrderDto;
import ruslan.shastkiv.bookstore.dto.order.OrderItemDto;
import ruslan.shastkiv.bookstore.exception.OrderProcessingException;
import ruslan.shastkiv.bookstore.mapper.OrderItemMapper;
import ruslan.shastkiv.bookstore.mapper.OrderMapper;
import ruslan.shastkiv.bookstore.mapper.OrderMapperImpl;
import ruslan.shastkiv.bookstore.model.Order;
import ruslan.shastkiv.bookstore.model.OrderItem;
import ruslan.shastkiv.bookstore.model.ShoppingCart;
import ruslan.shastkiv.bookstore.model.User;
import ruslan.shastkiv.bookstore.repository.order.OrderItemRepository;
import ruslan.shastkiv.bookstore.repository.order.OrderRepository;
import ruslan.shastkiv.bookstore.service.cart.ShoppingCartService;
import ruslan.shastkiv.bookstore.service.order.OrderServiceImpl;
import ruslan.shastkiv.bookstore.service.user.UserService;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @InjectMocks
    private OrderServiceImpl orderService;
    @Mock
    private ShoppingCartService shoppingCartService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserService userService;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Spy
    private OrderItemMapper orderItemMapper = Mappers.getMapper(OrderItemMapper.class);
    @Spy
    private OrderMapper orderMapper = new OrderMapperImpl(orderItemMapper);

    @Test
    @DisplayName("""
            placeOrderByUserId()
            - Should place an order by user ID and return OrderDto
            """)
    public void placeOrderByUserId_validRequest_returnOrderDto() {
        User user = createUser(USER_ID);
        Authentication authentication = getAuthentication(user);
        ShoppingCart cart = createCartWithItems(user, List.of(FIRST_BOOK_ID));
        Order order = createOrder(user, List.of(FIRST_BOOK_ID));
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        when(userService.getUserId(authentication)).thenReturn(USER_ID);
        when(shoppingCartService.findShoppingCart(USER_ID)).thenReturn(cart);
        when(orderRepository.save(orderCaptor.capture())).thenReturn(order);
        OrderDto actualDto = orderService.placeOrderByUserId(
                authentication, createPlaceOrderRequestDto(USER_ID));
        OrderDto expectedDto = createOrderDto(
                USER_ID, List.of(FIRST_BOOK_ID), Order.Status.PENDING);
        Order capturedOrder = orderCaptor.getValue();

        assertEquals(order.getUser(), capturedOrder.getUser());
        verify(shoppingCartService, times(ONE_INVOCATION)).clearShoppingCart(cart);
        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("""
            placeOrderByUserId()
            - Should return a correct total price
            """)
    public void placeOrderByUserId_validRequest_returnCorrectTotalPrice() {
        User user = createUser(USER_ID);
        Authentication authentication = getAuthentication(user);
        ShoppingCart cart = createCartWithItems(
                user, List.of(FIRST_BOOK_ID, SECOND_BOOK_ID, THIRD_BOOK_ID));
        Order order = createOrder(
                user, List.of(FIRST_BOOK_ID, SECOND_BOOK_ID, THIRD_BOOK_ID));

        when(userService.getUserId(authentication)).thenReturn(USER_ID);
        when(shoppingCartService.findShoppingCart(USER_ID)).thenReturn(cart);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        OrderDto actualDto = orderService.placeOrderByUserId(
                authentication, createPlaceOrderRequestDto(USER_ID));

        assertEquals(EXPECTED_PRICE, actualDto.total().intValue());
    }

    @Test
    @DisplayName("""
            placeOrderByUserId()
            - Should throw an exception when shopping cart is empty
            """)
    public void placeOrderByUserId_emptyCart_throwsException() {
        User user = createUser(USER_ID);
        Authentication authentication = getAuthentication(user);
        ShoppingCart cart = createCartWithItems(user, List.of());

        when(userService.getUserId(authentication)).thenReturn(USER_ID);
        when(shoppingCartService.findShoppingCart(USER_ID)).thenReturn(cart);

        assertThrows(OrderProcessingException.class, () -> orderService.placeOrderByUserId(
                authentication, createPlaceOrderRequestDto(USER_ID)));
    }

    @Test
    @DisplayName("""
            getAllOrdersByUserId()
            - Should get all orders by user ID
            """)
    public void getAllOrdersByUserId_validRequest_returnPageableOrderDto() {
        OrderDto orderDto = createOrderDto(
                USER_ID, List.of(FIRST_BOOK_ID), Order.Status.PENDING);
        PageImpl<OrderDto> expectedPage = new PageImpl<>(List.of(orderDto), PAGEABLE, PAGE_SIZE_1);
        Order order = createOrder(
                createUser(USER_ID), List.of(FIRST_BOOK_ID));
        Page<Order> orderPage = new PageImpl<>(List.of(order), PAGEABLE, PAGE_SIZE_1);

        when(orderRepository.findAllByUserId(USER_ID, PAGEABLE)).thenReturn(orderPage);
        Page<OrderDto> actualPage = orderService.getAllOrdersByUserId(USER_ID, PAGEABLE);

        assertEquals(expectedPage, actualPage);
    }

    @Test
    @DisplayName("""
            updateOrderStatus()
            - Should update order status
            """)
    public void updateOrderStatus_validRequest_returnOrderDto() {
        Order order = createOrder(createUser(USER_ID), List.of(FIRST_BOOK_ID));
        Order updatedOrder = createOrder(createUser(USER_ID), List.of(FIRST_BOOK_ID));
        updatedOrder.setStatus(Order.Status.PROCESSING);
        OrderDto expectedDto = createOrderDto(
                USER_ID, List.of(FIRST_BOOK_ID), Order.Status.PROCESSING);

        when(orderRepository.findById(ORDER_ID_1)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(updatedOrder);
        OrderDto actualDto = orderService.updateOrderStatus(
                ORDER_ID_1, createUpdateStatusDto(Order.Status.PROCESSING));

        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("""
            getOrderItemsByOrderId()
            - Should get order items by order ID
            """)
    public void getOrderItemsByOrderId_validRequest_returnPageableOrderItemDto() {
        OrderItem orderItem = createOrderItem(FIRST_BOOK_ID);
        PageImpl<OrderItem> orderItems
                = new PageImpl<>(List.of(orderItem), PAGEABLE, PAGE_SIZE_1);
        OrderItemDto orderItemDto = createOrderItemDto(FIRST_BOOK_ID);
        PageImpl<OrderItemDto> expectedPage
                = new PageImpl<>(List.of(orderItemDto), PAGEABLE, PAGE_SIZE_1);

        when(orderItemRepository.findAllByOrderId(ORDER_ID_1, PAGEABLE)).thenReturn(orderItems);
        Page<OrderItemDto> actualPage = orderService.getOrderItemsByOrderId(ORDER_ID_1, PAGEABLE);

        assertEquals(expectedPage, actualPage);
    }

    @Test
    @DisplayName("""
            getOrderItem()
            - Should get order item by ID
            """)
    public void getOrderItem_validRequest_returnOrderItemDto() {
        OrderItemDto orderItemDto = createOrderItemDto(FIRST_BOOK_ID);
        OrderItem orderItem = createOrderItem(FIRST_BOOK_ID);

        when(orderItemRepository.findByIdAndOrderId(ORDER_ITEM_ID_1, ORDER_ID_1))
                .thenReturn(Optional.of(orderItem));

        OrderItemDto actualDto = orderService.getOrderItem(ORDER_ID_1, ORDER_ITEM_ID_1);

        assertEquals(orderItemDto, actualDto);
    }
}
