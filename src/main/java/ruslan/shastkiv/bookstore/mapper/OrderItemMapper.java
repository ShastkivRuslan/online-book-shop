package ruslan.shastkiv.bookstore.mapper;

import ruslan.shastkiv.bookstore.dto.order.OrderItemDto;
import ruslan.shastkiv.bookstore.model.OrderItem;

public interface OrderItemMapper {
    OrderItemDto toDto(OrderItem orderItem);

    OrderItem toModel(OrderItemDto orderItemDto);
}
