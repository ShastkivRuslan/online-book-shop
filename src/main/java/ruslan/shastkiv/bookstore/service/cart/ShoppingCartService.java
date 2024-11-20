package ruslan.shastkiv.bookstore.service.cart;

import jakarta.validation.Valid;
import ruslan.shastkiv.bookstore.dto.cart.ShoppingCartDto;
import ruslan.shastkiv.bookstore.dto.item.CartItemRequestDto;
import ruslan.shastkiv.bookstore.dto.item.UpdateCartItemRequestDto;
import ruslan.shastkiv.bookstore.model.ShoppingCart;
import ruslan.shastkiv.bookstore.model.User;

public interface ShoppingCartService {
    void createShoppingCart(User user);

    ShoppingCartDto getShoppingCart(Long userId);

    ShoppingCartDto addBookToCart(Long userId, CartItemRequestDto requestDto);

    ShoppingCartDto updateItemQuantity(Long userId,
                                   Long cartItemId,
                                   @Valid UpdateCartItemRequestDto requestDto);

    void removeCartItem(Long userId, Long cartItemId);

    ShoppingCart findShoppingCart(Long id);

    void clearShoppingCart(ShoppingCart shoppingCart);
}
