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
    public static final Long ITEM_ID_3 = 3L;
    public static final Long INVALID_ITEM_ID = 100L;

    /**
     * Creates a {@link CartItemDto} with the specified item ID.
     * The book title is generated using the provided item ID, and the quantity
     * is set to the integer value of the ID.
     * @param id the ID of the cart item
     * @return a {@link CartItemDto} containing the specified item ID,
     * a formatted book title, and the quantity
     */
    public static CartItemDto createCartItemDto(Long id) {
        return new CartItemDto(id,
                id,
                CUSTOM_BOOK_TITLE.formatted(id),
                id.intValue());
    }

    /**
     * Creates a {@link CartItem} with the specified item ID and associated shopping cart.
     * The book for the cart item is created using the provided item ID,
     * and the quantity is set to the integer value of the ID.
     *
     * @param id    the ID of the cart item
     * @param cart  the {@link ShoppingCart} to which the cart item belongs
     * @return a {@link CartItem} object with the specified ID,
     * associated shopping cart, and book details
     */
    public static CartItem createCartItem(Long id, ShoppingCart cart) {
        CartItem cartItem = new CartItem();
        cartItem.setId(id);
        cartItem.setShoppingCart(cart);
        cartItem.setBook(createBookById(id, List.of()));
        cartItem.setQuantity(id.intValue());
        return cartItem;
    }

    /**
     * Creates a {@link ShoppingCart} with the specified cart ID, user, and set of cart items.
     * This method initializes a new shopping cart and sets the
     * given values for its ID, user, and cart items.
     *
     * @param cartId the ID of the shopping cart
     * @param user   the user associated with the shopping cart
     * @param items  the set of {@link CartItem} objects to be included in the cart
     * @return a {@link ShoppingCart} object with the specified ID, user, and cart items
     */

    public static ShoppingCart createCart(Long cartId, User user, Set<CartItem> items) {
        ShoppingCart cart = new ShoppingCart();
        cart.setId(cartId);
        cart.setUser(user);
        cart.setCartItems(items);
        return cart;
    }

    /**
     * Creates a {@link ShoppingCartDto} for the specified user with a list of cart item IDs.
     * This method maps the provided list of cart item IDs to {@link CartItemDto} objects and
     * creates a new shopping cart DTO with the given user ID and the corresponding
     * set of cart items.
     *
     * @param userId       the ID of the user owning the shopping cart
     * @param cartItemIds  a list of cart item IDs to include in the shopping cart
     * @return a {@link ShoppingCartDto} containing the user ID and a set of {@link CartItemDto}
     */
    public static ShoppingCartDto createShoppingCartDto(Long userId, List<Long> cartItemIds) {
        return new ShoppingCartDto(
                userId,
                cartItemIds.stream()
                        .map(ShoppingCartTestUtils::createCartItemDto)
                        .collect(Collectors.toSet())
        );
    }

    /**
     * Creates a {@link CartItemRequestDto} with the specified item ID.
     * The quantity is set to the integer value of the provided ID(for simplify).
     *
     * @param id the ID of the cart item
     * @return a {@link CartItemRequestDto} containing the specified item ID and its quantity
     */
    public static CartItemRequestDto createCartItemRequestDto(Long id) {
        return new CartItemRequestDto(
                id,
                id.intValue()
        );
    }

    /**
     * Creates an {@link UpdateCartItemRequestDto} with the specified quantity.
     *
     * @param quantity the quantity to set for the cart item
     * @return an {@link UpdateCartItemRequestDto} containing the specified quantity
     */
    public static UpdateCartItemRequestDto createUpdateCartItemDto(int quantity) {
        return new UpdateCartItemRequestDto(quantity);
    }

    /**
     * Creates an updated shopping cart DTO with a specified cart item.
     *
     * @param userId   the ID of the user owning the shopping cart
     * @param itemId   the ID of the cart item to include in the shopping cart
     * @param quantity the quantity of the specified cart item
     * @return a {@link ShoppingCartDto} containing the updated cart item
     */
    public static ShoppingCartDto createUpdatedShoppingCartDto(
            Long userId, Long itemId, int quantity) {
        CartItemDto cartItemDto = new CartItemDto(
                itemId,
                userId,
                CUSTOM_BOOK_TITLE.formatted(itemId),
                quantity);

        return new ShoppingCartDto(userId, Set.of(cartItemDto));
    }

    /**
     * Updates the quantity of a specific book in the shopping cart.
     *
     * @param cart       the shopping cart containing the items
     * @param bookId     the ID of the book whose quantity should be updated
     * @param newQuantity the new quantity to set for the specified book
     */
    public static void updateCartItemQuantity(ShoppingCart cart, Long bookId, int newQuantity) {
        cart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(bookId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(newQuantity));
    }

    /**
     * Creates a {@link ShoppingCart} for the specified {@link User} and populates it
     * with {@link CartItem}s based on the provided list of book IDs.
     * Each item in the cart is initialized using the same ID as the book
     * and the quantity is set to the book ID value for simplicity.
     *
     * <p>The created {@link ShoppingCart} will have the same ID as the provided user.
     *
     * @param user    the {@link User} for whom the shopping cart is created
     * @param bookIds a list of book IDs to be added to the cart as items
     * @return a {@link ShoppingCart} instance with the specified user and populated items.
     */
    public static ShoppingCart createCartWithItems(User user, List<Long> bookIds) {
        ShoppingCart cart = new ShoppingCart();
        cart.setId(user.getId());
        cart.setUser(user);
        cart.setCartItems(
                bookIds.stream()
                        .map(id -> createCartItem(id, cart))
                        .collect(Collectors.toSet()));
        return cart;
    }
}
