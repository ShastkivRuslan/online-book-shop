package ruslan.shastkiv.bookstore.service.item;

import jakarta.validation.Valid;
import ruslan.shastkiv.bookstore.dto.item.CartItemDto;
import ruslan.shastkiv.bookstore.dto.item.CartItemRequestDto;
import ruslan.shastkiv.bookstore.dto.item.UpdateCartItemRequestDto;
import ruslan.shastkiv.bookstore.model.CartItem;
import ruslan.shastkiv.bookstore.model.ShoppingCart;

public interface CartItemService {
    CartItem addCartItem(ShoppingCart shoppingCart, CartItemRequestDto requestDto);

    CartItemDto updateItemQuantity(Long userId,
                                   Long cartItemId,
                                   @Valid UpdateCartItemRequestDto requestDto);

    void removeCartItem(Long userId, Long cartItemId);
}
