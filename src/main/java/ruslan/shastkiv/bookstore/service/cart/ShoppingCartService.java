package ruslan.shastkiv.bookstore.service.cart;

import jakarta.validation.Valid;
import ruslan.shastkiv.bookstore.dto.cart.ShoppingCartDto;
import ruslan.shastkiv.bookstore.dto.item.CartItemDto;
import ruslan.shastkiv.bookstore.dto.item.CartItemRequestDto;
import ruslan.shastkiv.bookstore.model.ShoppingCart;
import ruslan.shastkiv.bookstore.model.User;

public interface ShoppingCartService {
    void createShoppingCart(User user);

    ShoppingCartDto getShoppingCart(Long userId);

    CartItemDto addBookToCart(Long userId, @Valid CartItemRequestDto requestDto);

    ShoppingCart findShoppingCart(Long id);
}
