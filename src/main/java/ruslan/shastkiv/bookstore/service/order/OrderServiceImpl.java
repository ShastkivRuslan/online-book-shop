package ruslan.shastkiv.bookstore.service.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ruslan.shastkiv.bookstore.dto.order.OrderDto;
import ruslan.shastkiv.bookstore.dto.order.OrderItemDto;
import ruslan.shastkiv.bookstore.dto.order.PlaceOrderRequestDto;
import ruslan.shastkiv.bookstore.dto.order.UpdateOrderStatusRequestDto;
import ruslan.shastkiv.bookstore.exception.EmtyShoppingCartException;
import ruslan.shastkiv.bookstore.exception.EnptyShoppingCartException;
import ruslan.shastkiv.bookstore.exception.EntityNotFoundException;
import ruslan.shastkiv.bookstore.mapper.OrderItemMapper;
import ruslan.shastkiv.bookstore.mapper.OrderMapper;
import ruslan.shastkiv.bookstore.model.Order;
import ruslan.shastkiv.bookstore.model.OrderItem;
import ruslan.shastkiv.bookstore.model.ShoppingCart;
import ruslan.shastkiv.bookstore.model.User;
import ruslan.shastkiv.bookstore.repository.order.OrderItemRepository;
import ruslan.shastkiv.bookstore.repository.order.OrderRepository;
import ruslan.shastkiv.bookstore.service.cart.ShoppingCartService;
import ruslan.shastkiv.bookstore.service.user.UserService;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ShoppingCartService shoppingCartService;
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional
    public OrderDto placeOrderByUserId(Authentication authentication, PlaceOrderRequestDto requestDto) throws EnptyShoppingCartException {
        User user = userService.getUser(authentication);
        ShoppingCart userShoppingCart = shoppingCartService.findShoppingCart(user.getId());
        checkIsEmptyShoppingCart(userShoppingCart);
        Order userOrder = initOrder(userShoppingCart, requestDto);
        Set<OrderItem> orderItems = createOrderItems(userShoppingCart, userOrder);
        userOrder.setOrderItems(orderItems);
        userOrder.setTotal(calculateTotalPrice(orderItems));
        return orderMapper.toDto(orderRepository.save(userOrder));
    }

    @Override
    public Page<OrderDto> getAllOrdersByUserId(Long userId, Pageable pageable) {
        return orderRepository.findAllByUserId(userId, pageable).map(orderMapper::toDto);
    }

    @Override
    public OrderDto updateOrderStatus(Long orderId, UpdateOrderStatusRequestDto statusRequestDto) {
        Order order = findOrderById(orderId);
        order.setStatus(statusRequestDto.status());
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public Page<OrderItemDto> getOrderItemsByOrderId(Long orderId, Pageable pageable) {
        return orderItemRepository.findAllByOrderId(orderId, pageable).map(orderItemMapper::toDto);
    }

    @Override
    public OrderItemDto getOrderItem(Long orderId, Long itemId) {
        OrderItem orderItem = orderItemRepository.findByIdAndOrderId(itemId, orderId).orElseThrow(()
                -> new EntityNotFoundException("Order item with ID [" + itemId
                + "] not found in order ID [" + orderId + "]."));
        return orderItemMapper.toDto(orderItem);
    }

    private Order initOrder(ShoppingCart userShoppingCart, PlaceOrderRequestDto requestDto) {
        Order order = orderMapper.toOrder(userShoppingCart);
        order.setStatus(Order.Status.PENDING);
        order.setShippingAddress(requestDto.shippingAddress());
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    private Set<OrderItem> createOrderItems(ShoppingCart userShoppingCart, Order order) {
        return userShoppingCart.getCartItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = orderItemMapper.toOrderItem(cartItem);
                    orderItem.setOrder(order);
                    return orderItem;
                } )
                .collect(Collectors.toSet());
    }

    private BigDecimal calculateTotalPrice(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem -> orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void checkIsEmptyShoppingCart(ShoppingCart userShoppingCart) {
        if (userShoppingCart.getCartItems().isEmpty()) {
            throw new EmtyShoppingCartException(
                    "Shopping cart is empty for user: [" + userShoppingCart.getId() + "]"
            );
        }
    }

    private Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(()
                -> new EntityNotFoundException("Can t find order by id: [" + orderId + "]"));
    }
}
