package ruslan.shastkiv.bookstore.dto.cart;

public record ShoppingCartDto(String userId,
                              Set<CartItemDto> cartItems) {
}
