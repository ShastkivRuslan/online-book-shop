package ruslan.shastkiv.bookstore.dto.order;

import ruslan.shastkiv.bookstore.model.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(Long id,
                       Long userId,
                       List<OrderItemDto> orderItems,
                       LocalDateTime orderDate,
                       BigDecimal total,
                       Order.Status status) {
}
