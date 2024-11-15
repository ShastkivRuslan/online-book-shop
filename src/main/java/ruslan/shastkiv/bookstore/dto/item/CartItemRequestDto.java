package ruslan.shastkiv.bookstore.dto.item;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemRequestDto(@NotNull(message = "Book ID cannot be null")
                                 @Positive(message = "Book ID must be greater than 0")
                                 Long bookId,
                                 @Positive(message = "Quantity must be at least 1")
                                 int quantity) {
}
