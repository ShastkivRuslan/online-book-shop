package ruslan.shastkiv.bookstore.dto.order;

import ruslan.shastkiv.bookstore.model.Order;

public record UpdateOrderStatusRequestDto(Order.Status status) {
}
//todo: create validation to validate is request status is present in enum
