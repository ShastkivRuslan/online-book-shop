package ruslan.shastkiv.bookstore.dto.order;

import ruslan.shastkiv.bookstore.model.Order;

public record UpdateOrderStatusRequestDto(Order.Status status) {
}
