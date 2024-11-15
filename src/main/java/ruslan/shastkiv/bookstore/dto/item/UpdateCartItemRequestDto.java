package ruslan.shastkiv.bookstore.dto.item;

import jakarta.validation.constraints.Positive;

public record UpdateCartItemRequestDto(@Positive(message = "Quantity must be at least 1")
                                       int quantity) {
}
