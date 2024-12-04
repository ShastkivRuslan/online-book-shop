package ruslan.shastkiv.bookstore.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.FIRST_BOOK_ID;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.PAGEABLE;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.PAGE_SIZE_1;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.ONE_INVOCATION;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.ORDER_ID_1;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.ORDER_ITEM_ID_1;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.createOrder;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.createOrderDto;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.createOrderItem;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.createOrderItemDto;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.createPlaceOrderRequestDto;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.createUpdateStatusDto;
import static ruslan.shastkiv.bookstore.utils.ShoppingCartTestUtils.createCart;
import static ruslan.shastkiv.bookstore.utils.ShoppingCartTestUtils.createCartItem;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.USER_ID;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.createUser;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.getAuthentication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.Authentication;
import ruslan.shastkiv.bookstore.dto.order.OrderDto;
import ruslan.shastkiv.bookstore.dto.order.OrderItemDto;
import ruslan.shastkiv.bookstore.mapper.OrderItemMapper;
import ruslan.shastkiv.bookstore.mapper.OrderMapper;
import ruslan.shastkiv.bookstore.model.CartItem;
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
    private OrderMapper orderMapper;
    @Mock
    private OrderItemMapper orderItemMapper;
    @Mock
    private OrderItemRepository orderItemRepository;

    @Test
    @DisplayName("""
            Should place an order by user ID and return OrderDto
            """)
    public void placeOrderByUserId_validRequest_returnOrderDto() {
        User user = createUser(USER_ID);
        Authentication authentication = getAuthentication(user);
        ShoppingCart cart = createCart(USER_ID, user, new HashSet<>());
        CartItem cartItem = createCartItem(FIRST_BOOK_ID, cart);
        cart.getCartItems().add(cartItem);
        Order order = createOrder(user, List.of());
        OrderItem orderItem = createOrderItem(order, FIRST_BOOK_ID);
        order.getOrderItems().add(orderItem);
        OrderDto expectedDto = createOrderDto(ORDER_ID_1, List.of(
                createOrderItemDto(FIRST_BOOK_ID)), Order.Status.PENDING);

        when(userService.getUserId(authentication)).thenReturn(USER_ID);
        when(shoppingCartService.findShoppingCart(USER_ID)).thenReturn(cart);
        when(orderRepository.save(any(Order.class))).thenReturn(order);//todo
        when(orderMapper.toDto(any(Order.class))).thenReturn(expectedDto);
        when(orderMapper.toOrder(any(ShoppingCart.class))).thenReturn(order);
        when(orderItemMapper.toOrderItem(cartItem)).thenReturn(orderItem);

        OrderDto actualDto = orderService.placeOrderByUserId(
                authentication, createPlaceOrderRequestDto(USER_ID));

        verify(shoppingCartService, times(ONE_INVOCATION)).clearShoppingCart(cart);
        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("""
            Get all orders by user ID
            """)
    public void getAllOrdersByUserId_validRequest_returnPageableOrderDto() {
        OrderItemDto orderItemDto = createOrderItemDto(ORDER_ITEM_ID_1);
        OrderDto orderDto = createOrderDto(ORDER_ID_1, List.of(orderItemDto), Order.Status.PENDING);
        Order order = createOrder(createUser(USER_ID), new ArrayList<>());
        OrderItem orderItem = createOrderItem(order, FIRST_BOOK_ID);
        order.getOrderItems().add(orderItem);
        Page<Order> orderPage = new PageImpl<>(List.of(order), PAGEABLE, PAGE_SIZE_1);
        PageImpl<OrderDto> expectedPage = new PageImpl<>(List.of(orderDto), PAGEABLE, PAGE_SIZE_1);

        when(orderMapper.toDto(any(Order.class))).thenReturn(orderDto);
        when(orderRepository.findAllByUserId(USER_ID, PAGEABLE)).thenReturn(orderPage);
        Page<OrderDto> actualPage = orderService.getAllOrdersByUserId(USER_ID, PAGEABLE);

        assertEquals(expectedPage, actualPage);
    }

    @Test
    @DisplayName("""
            Update order status
            """)
    public void updateOrderStatus_validRequest_returnOrderDto() {
        Order order = createOrder(createUser(USER_ID), List.of());
        order.setStatus(Order.Status.PROCESSING);
        OrderDto expectedDto = createOrderDto(ORDER_ID_1, List.of(), Order.Status.PROCESSING);

        when(orderRepository.findById(ORDER_ID_1)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDto(any(Order.class))).thenReturn(expectedDto);
        OrderDto actualDto = orderService.updateOrderStatus(
                ORDER_ID_1, createUpdateStatusDto(Order.Status.PROCESSING));

        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("""
            Get order items by order ID
            """)
    public void getOrderItemsByOrderId_validRequest_returnPageableOrderItemDto() {
        Order order = createOrder(createUser(USER_ID), new ArrayList<>());
        OrderItem orderItem = createOrderItem(order, FIRST_BOOK_ID);
        order.getOrderItems().add(orderItem);
        OrderItemDto orderItemDto = createOrderItemDto(ORDER_ITEM_ID_1);
        PageImpl<OrderItem> orderItems
                = new PageImpl<>(List.of(orderItem), PAGEABLE, PAGE_SIZE_1);
        PageImpl<OrderItemDto> expectedPage
                = new PageImpl<>(List.of(orderItemDto), PAGEABLE, PAGE_SIZE_1);

        when(orderItemRepository.findAllByOrderId(ORDER_ID_1, PAGEABLE)).thenReturn(orderItems);
        when(orderItemMapper.toDto(any(OrderItem.class))).thenReturn(orderItemDto);
        Page<OrderItemDto> actualPage = orderService.getOrderItemsByOrderId(ORDER_ID_1, PAGEABLE);

        assertEquals(expectedPage, actualPage);
    }

    @Test
    @DisplayName("""
            Get order item by ID
            """)
    public void getOrderItem_validRequest_returnOrderItemDto() {
        OrderItemDto orderItemDto = createOrderItemDto(ORDER_ITEM_ID_1);
        Order order = createOrder(createUser(USER_ID), new ArrayList<>());
        OrderItem orderItem = createOrderItem(order, FIRST_BOOK_ID);
        order.getOrderItems().add(orderItem);

        when(orderItemRepository.findByIdAndOrderId(ORDER_ITEM_ID_1, ORDER_ID_1))
                .thenReturn(Optional.of(orderItem));
        when(orderItemMapper.toDto(any(OrderItem.class))).thenReturn(orderItemDto);

        OrderItemDto actualDto = orderService.getOrderItem(ORDER_ID_1, ORDER_ITEM_ID_1);

        assertEquals(orderItemDto, actualDto);
    }
}
