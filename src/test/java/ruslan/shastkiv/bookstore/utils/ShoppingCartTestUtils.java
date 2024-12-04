package ruslan.shastkiv.bookstore.utils;

import static ruslan.shastkiv.bookstore.utils.BookTestUtils.CUSTOM_BOOK_TITLE;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.createBookById;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import ruslan.shastkiv.bookstore.dto.cart.ShoppingCartDto;
import ruslan.shastkiv.bookstore.dto.item.CartItemDto;
import ruslan.shastkiv.bookstore.dto.item.CartItemRequestDto;
import ruslan.shastkiv.bookstore.dto.item.UpdateCartItemRequestDto;
import ruslan.shastkiv.bookstore.model.CartItem;
import ruslan.shastkiv.bookstore.model.ShoppingCart;
import ruslan.shastkiv.bookstore.model.User;

public class ShoppingCartTestUtils {
    public static final String SHOPPING_CART_URL = "/cart";
    public static final String ITEM_URL = "/cart/items/";
    public static final int ITEM_ID_FOR_DELETE = 3;
    public static final int UPDATED_QUANTITY = 20;
    public static final Long ITEM_ID = 3L;

    public static CartItemDto createCartItemDto(Long id) {
        return new CartItemDto(id,
                id,
                CUSTOM_BOOK_TITLE.formatted(id),
                id.intValue());
    }

    public static CartItem createCartItem(Long id, ShoppingCart cart) {
        CartItem cartItem = new CartItem();
        cartItem.setId(id);
        cartItem.setShoppingCart(cart);
        cartItem.setBook(createBookById(id, List.of()));
        cartItem.setQuantity(id.intValue());
        return cartItem;
    }

    public static ShoppingCart createCart(Long cartId, User user, Set<CartItem> items) {
        ShoppingCart cart = new ShoppingCart();
        cart.setId(cartId);
        cart.setUser(user);
        cart.setCartItems(items);
        return cart;
    }

    public static ShoppingCartDto createShoppingCartDto(Long userId, List<Long> cartItemIds) {
        return new ShoppingCartDto(
                userId,
                cartItemIds.stream()
                        .map(ShoppingCartTestUtils::createCartItemDto)
                        .collect(Collectors.toSet())
        );
    }

    public static CartItemRequestDto createCartItemRequestDto(Long id) {
        return new CartItemRequestDto(
                id,
                id.intValue()
        );
    }

    public static UpdateCartItemRequestDto createUpdateCartItemDto(int quantity) {
        return new UpdateCartItemRequestDto(quantity);
    }

    public static ShoppingCartDto createUpdatedShoppingCartDto(
            Long userId, Long itemId, int quantity) {
        CartItemDto cartItemDto = new CartItemDto(
                itemId,
                userId,
                CUSTOM_BOOK_TITLE.formatted(itemId),
                quantity);

        return new ShoppingCartDto(userId, Set.of(cartItemDto));
    }

}
