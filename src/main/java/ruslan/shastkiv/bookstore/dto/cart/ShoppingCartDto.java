package ruslan.shastkiv.bookstore.dto.cart;

import ruslan.shastkiv.bookstore.dto.item.CartItemDto;

import java.util.Set;

public record ShoppingCartDto(String userId,
                              Set<CartItemDto> cartItems) {
}
