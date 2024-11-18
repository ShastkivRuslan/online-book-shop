package ruslan.shastkiv.bookstore.service.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ruslan.shastkiv.bookstore.dto.order.OrderDto;
import ruslan.shastkiv.bookstore.dto.order.OrderItemDto;
import ruslan.shastkiv.bookstore.dto.order.UpdateOrderStatusRequestDto;

public interface OrderService {
    OrderDto placeOrderByUserId(Long userId);

    Page<OrderDto> getAllOrdersByUserId(Long userId, Pageable pageable);

    OrderDto updateOrderStatus(Long orderId, UpdateOrderStatusRequestDto statusRequestDto);

    Page<OrderItemDto> getOrderItemsByOrderId(Long orderId, Pageable pageable);

    OrderItemDto getOrderItem(Long orderId, Long itemId);
}
