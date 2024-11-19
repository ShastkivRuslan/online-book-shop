package ruslan.shastkiv.bookstore.service.cart;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ruslan.shastkiv.bookstore.dto.cart.ShoppingCartDto;
import ruslan.shastkiv.bookstore.dto.item.CartItemRequestDto;
import ruslan.shastkiv.bookstore.dto.item.UpdateCartItemRequestDto;
import ruslan.shastkiv.bookstore.exception.EntityNotFoundException;
import ruslan.shastkiv.bookstore.mapper.ShoppingCartMapper;
import ruslan.shastkiv.bookstore.model.CartItem;
import ruslan.shastkiv.bookstore.model.ShoppingCart;
import ruslan.shastkiv.bookstore.model.User;
import ruslan.shastkiv.bookstore.repository.cart.ShoppingCartRepository;
import ruslan.shastkiv.bookstore.repository.item.CartItemRepository;
import ruslan.shastkiv.bookstore.service.book.BookService;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemRepository cartItemRepository;
    private final BookService bookService;

    @Override
    public void createShoppingCart(User user) {
        ShoppingCart cart = new ShoppingCart();
        cart.setUser(user);
        shoppingCartRepository.save(cart);
    }

    @Override
    public ShoppingCartDto getShoppingCart(Long userId) {
        return shoppingCartMapper.toDto(findShoppingCart(userId));
    }

    @Override
    @Transactional
    public ShoppingCartDto addBookToCart(Long userId, CartItemRequestDto requestDto) {
        ShoppingCart shoppingCart = findShoppingCart(userId);
        return shoppingCartMapper.toDto(addCartItem(shoppingCart, requestDto));
    }

    @Override
    public void removeCartItem(Long userId, Long cartItemId) {
        CartItem cartItem = getByCartItemIdAndUserId(userId, cartItemId);
        cartItemRepository.delete(cartItem);
    }

    @Override
    @Transactional
    public ShoppingCartDto updateItemQuantity(Long userId,
                                               Long cartItemId,
                                               UpdateCartItemRequestDto requestDto) {
        CartItem cartItem = getByCartItemIdAndUserId(userId, cartItemId);
        cartItem.setQuantity(requestDto.quantity());
        return shoppingCartMapper.toDto(
                shoppingCartRepository.save(cartItem.getShoppingCart()));
    }

    private CartItem getByCartItemIdAndUserId(Long userId, Long cartItemId) {
        return cartItemRepository.findByIdAndShoppingCartId(cartItemId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cant find cart item with ID [" + cartItemId
                                + "] in the shopping cart for user ID [" + userId + "]."));
    }

    @Override
    public ShoppingCart findShoppingCart(Long id) {
        return shoppingCartRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Cant find shopping cart by user id: [" + id + "]"));
    }

    @Override
    public void clearShoppingCart(ShoppingCart shoppingCart) {
        shoppingCart.getCartItems().clear();
        shoppingCartRepository.save(shoppingCart);
    }

    private ShoppingCart addCartItem(ShoppingCart shoppingCart, CartItemRequestDto requestDto) {
        CartItem cartItem = cartItemRepository
                .findByBookIdAndShoppingCartId(requestDto.bookId(), shoppingCart.getId())
                .orElseGet(() -> {
                    CartItem newCartItem = new CartItem();
                    newCartItem.setBook(bookService.findBookById(requestDto.bookId()));
                    newCartItem.setShoppingCart(shoppingCart);
                    shoppingCart.getCartItems().add(newCartItem);
                    return newCartItem;
                });
        cartItem.setQuantity(cartItem.getQuantity() + requestDto.quantity());
        return shoppingCartRepository.save(shoppingCart);
    }
}
