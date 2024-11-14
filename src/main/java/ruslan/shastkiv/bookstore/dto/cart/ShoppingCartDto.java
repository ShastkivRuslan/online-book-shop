package ruslan.shastkiv.bookstore.dto.cart;

import java.util.Set;
import ruslan.shastkiv.bookstore.dto.item.CartItemDto;

public record ShoppingCartDto(Long userId,
                              Set<CartItemDto> cartItems) {
}
