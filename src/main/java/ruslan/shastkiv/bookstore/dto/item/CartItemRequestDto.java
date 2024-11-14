package ruslan.shastkiv.bookstore.dto.item;

public record CartItemRequestDto(Long bookId,
                                 int quantity) {
}
