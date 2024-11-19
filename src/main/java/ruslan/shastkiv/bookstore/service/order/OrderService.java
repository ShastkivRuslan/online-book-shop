package ruslan.shastkiv.bookstore.service.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import ruslan.shastkiv.bookstore.dto.order.OrderDto;
import ruslan.shastkiv.bookstore.dto.order.OrderItemDto;
import ruslan.shastkiv.bookstore.dto.order.PlaceOrderRequestDto;
import ruslan.shastkiv.bookstore.dto.order.UpdateOrderStatusRequestDto;
import ruslan.shastkiv.bookstore.exception.EnptyShoppingCartException;

public interface OrderService {
    OrderDto placeOrderByUserId(Authentication authentication, PlaceOrderRequestDto requestDto) throws EnptyShoppingCartException;

    Page<OrderDto> getAllOrdersByUserId(Long userId, Pageable pageable);

    OrderDto updateOrderStatus(Long orderId, UpdateOrderStatusRequestDto statusRequestDto);

    Page<OrderItemDto> getOrderItemsByOrderId(Long orderId, Pageable pageable);

    OrderItemDto getOrderItem(Long orderId, Long itemId);
}
