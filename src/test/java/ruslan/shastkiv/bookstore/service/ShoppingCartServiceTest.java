package ruslan.shastkiv.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.FIRST_BOOK_ID;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.THIRD_BOOK_ID;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.createBookById;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.FIRST_CATEGORY_ID;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.ONE_INVOCATION;
import static ruslan.shastkiv.bookstore.utils.ShoppingCartTestUtils.ITEM_ID_3;
import static ruslan.shastkiv.bookstore.utils.ShoppingCartTestUtils.UPDATED_QUANTITY;
import static ruslan.shastkiv.bookstore.utils.ShoppingCartTestUtils.createCart;
import static ruslan.shastkiv.bookstore.utils.ShoppingCartTestUtils.createCartItem;
import static ruslan.shastkiv.bookstore.utils.ShoppingCartTestUtils.createCartItemRequestDto;
import static ruslan.shastkiv.bookstore.utils.ShoppingCartTestUtils.createCartWithItems;
import static ruslan.shastkiv.bookstore.utils.ShoppingCartTestUtils.createShoppingCartDto;
import static ruslan.shastkiv.bookstore.utils.ShoppingCartTestUtils.createUpdateCartItemDto;
import static ruslan.shastkiv.bookstore.utils.ShoppingCartTestUtils.updateCartItemQuantity;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.INVALID_USER_ID;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.USER_ID;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.createUser;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ruslan.shastkiv.bookstore.dto.cart.ShoppingCartDto;
import ruslan.shastkiv.bookstore.dto.item.CartItemRequestDto;
import ruslan.shastkiv.bookstore.exception.EntityNotFoundException;
import ruslan.shastkiv.bookstore.mapper.BookMapperImpl;
import ruslan.shastkiv.bookstore.mapper.CartItemMapperImpl;
import ruslan.shastkiv.bookstore.mapper.CategoryMapperImpl;
import ruslan.shastkiv.bookstore.mapper.ShoppingCartMapper;
import ruslan.shastkiv.bookstore.mapper.ShoppingCartMapperImpl;
import ruslan.shastkiv.bookstore.model.Book;
import ruslan.shastkiv.bookstore.model.CartItem;
import ruslan.shastkiv.bookstore.model.ShoppingCart;
import ruslan.shastkiv.bookstore.model.User;
import ruslan.shastkiv.bookstore.repository.cart.ShoppingCartRepository;
import ruslan.shastkiv.bookstore.repository.item.CartItemRepository;
import ruslan.shastkiv.bookstore.service.book.BookService;
import ruslan.shastkiv.bookstore.service.cart.ShoppingCartServiceImpl;

@ExtendWith(SpringExtension.class)
public class ShoppingCartServiceTest {
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private BookService bookService;
    @Spy
    private ShoppingCartMapper shoppingCartMapper = new ShoppingCartMapperImpl(
            new CartItemMapperImpl(new BookMapperImpl(new CategoryMapperImpl())));

    @Test
    @DisplayName("""
            createShoppingCart()
            - Should create a shopping cart for the user
            """)
    public void createShoppingCart_validUser_saveShoppingCart() {
        User user = createUser(USER_ID);

        shoppingCartService.createShoppingCart(user);
        ArgumentCaptor<ShoppingCart> shoppingCartCaptor
                = ArgumentCaptor.forClass(ShoppingCart.class);

        verify(shoppingCartRepository, times(ONE_INVOCATION)).save(shoppingCartCaptor.capture());
        assertEquals(shoppingCartCaptor.getValue().getUser(), user);
    }

    @Test
    @DisplayName("""
            getShoppingCart()
            - Should retrieve the shopping cart by its ID
            """)
    public void getShoppingCart_validId_returnShoppingCartDto() {
        User user = createUser(USER_ID);
        ShoppingCart cart = createCart(USER_ID, user, Set.of());
        ShoppingCartDto shoppingCartDto = createShoppingCartDto(USER_ID, List.of());

        when(shoppingCartRepository.findById(USER_ID)).thenReturn(Optional.of(cart));
        //when(shoppingCartMapper.toDto(cart)).thenReturn(shoppingCartDto);
        ShoppingCartDto actualDto = shoppingCartService.getShoppingCart(USER_ID);

        assertEquals(shoppingCartDto, actualDto);
    }

    @Test
    @DisplayName("""
            addBookToCart()
            - Should add a book to the shopping cart and return ShoppingCartDto
            """)
    public void addBookToCart_validRequest_returnShoppingCartDto() {
        User user = createUser(USER_ID);
        CartItemRequestDto requestDto = createCartItemRequestDto(FIRST_BOOK_ID);
        ShoppingCart cart = createCart(USER_ID, user, Set.of());
        ShoppingCartDto shoppingCartDto = createShoppingCartDto(USER_ID, List.of(FIRST_BOOK_ID));
        Book book = createBookById(FIRST_BOOK_ID, List.of(FIRST_CATEGORY_ID));
        CartItem cartItem = createCartItem(FIRST_BOOK_ID, cart);

        when(shoppingCartRepository.findById(USER_ID)).thenReturn(Optional.of(cart));
        when(bookService.findBookById(requestDto.bookId())).thenReturn(book);
        when(cartItemRepository.findByBookIdAndShoppingCartId(FIRST_BOOK_ID, USER_ID))
                .thenReturn(Optional.of(cartItem));
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(cart);
        //when(shoppingCartMapper.toDto(cart)).thenReturn(shoppingCartDto);

        ShoppingCartDto result = shoppingCartService.addBookToCart(USER_ID, requestDto);
        assertNotNull(result);
        verify(shoppingCartRepository, times(ONE_INVOCATION)).findById(USER_ID);
        verify(shoppingCartRepository, times(ONE_INVOCATION)).save(any(ShoppingCart.class));
        verify(shoppingCartMapper, times(ONE_INVOCATION)).toDto(cart);
    }

    @Test
    @DisplayName("""
            removeCartItem()
            - Should remove an item from the shopping cart
            """)
    public void removeCartItem_validRequest_removeItemFromCart() {
        ShoppingCart cart = createCart(USER_ID, createUser(USER_ID), Set.of());
        CartItem cartItem = createCartItem(FIRST_BOOK_ID, cart);
        cart.setCartItems(Set.of(cartItem));
        when(cartItemRepository.findByIdAndShoppingCartId(FIRST_BOOK_ID, USER_ID))
                .thenReturn(Optional.of(cartItem));

        shoppingCartService.removeCartItem(USER_ID, FIRST_BOOK_ID);

        verify(cartItemRepository, times(ONE_INVOCATION)).delete(cartItem);
    }

    @Test
    @DisplayName("""
            updateItemQuantity()
            - Should update the quantity of an item in the shopping cart and return ShoppingCartDto
            """)
    public void updateItemQuantity_validRequest_returnShoppingCartDto() {
        ShoppingCart cart = createCartWithItems(createUser(USER_ID), List.of(THIRD_BOOK_ID));
        updateCartItemQuantity(cart, THIRD_BOOK_ID, UPDATED_QUANTITY);

        when(cartItemRepository.findByIdAndShoppingCartId(ITEM_ID_3, USER_ID))
                .thenReturn(Optional.of(createCartItem(THIRD_BOOK_ID, cart)));
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(cart);
        ShoppingCartDto actualDto = shoppingCartService.updateItemQuantity(
                USER_ID, ITEM_ID_3, createUpdateCartItemDto(UPDATED_QUANTITY));

        assertTrue(cart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(THIRD_BOOK_ID))
                .anyMatch(item -> item.getQuantity() == UPDATED_QUANTITY));
    }

    @Test
    @DisplayName("""
            findShoppingCart()
            - Should retrieve the shopping cart by its ID for a valid request
            """)
    public void findShoppingCart_validRequest_returnShoppingCartDto() {
        ShoppingCart expectedCart = createCart(USER_ID, createUser(USER_ID), Set.of());
        when(shoppingCartRepository.findById(USER_ID)).thenReturn(Optional.of(expectedCart));

        ShoppingCart actualCart = shoppingCartService.findShoppingCart(USER_ID);

        assertEquals(expectedCart, actualCart);
    }

    @Test
    @DisplayName("""
            findShoppingCart()
            - Should throw EntityNotFoundException for an invalid shopping cart ID
            """)
    public void findShoppingCart_invalidRequest_throwException() {
        when(shoppingCartRepository.findById(INVALID_USER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.findShoppingCart(INVALID_USER_ID));

    }

    @Test
    @DisplayName("""
            clearShoppingCart()
            - Should clear the shopping cart and save it
            """)
    public void clearShoppingCart_validRequest_saveClearedCart() {
        ShoppingCart cart = createCart(USER_ID, createUser(USER_ID), new HashSet<>());
        CartItem cartItem = createCartItem(FIRST_BOOK_ID, cart);
        cart.getCartItems().add(cartItem);

        shoppingCartService.clearShoppingCart(cart);
        ArgumentCaptor<ShoppingCart> shoppingCartCaptor
                = ArgumentCaptor.forClass(ShoppingCart.class);

        verify(shoppingCartRepository, times(ONE_INVOCATION)).save(shoppingCartCaptor.capture());
        assertTrue(shoppingCartCaptor.getValue().getCartItems().isEmpty());
    }
}
