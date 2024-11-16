package ruslan.shastkiv.bookstore.dto.item;

public record CartItemDto(Long id,
                          Long bookId,
                          String bookTitle,
                          int quantity) {
}
