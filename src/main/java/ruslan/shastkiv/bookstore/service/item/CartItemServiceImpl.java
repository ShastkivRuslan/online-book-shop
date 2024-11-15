package ruslan.shastkiv.bookstore.service.item;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ruslan.shastkiv.bookstore.dto.item.CartItemDto;
import ruslan.shastkiv.bookstore.dto.item.CartItemRequestDto;
import ruslan.shastkiv.bookstore.dto.item.UpdateCartItemRequestDto;
import ruslan.shastkiv.bookstore.exception.EntityNotFoundException;
import ruslan.shastkiv.bookstore.mapper.CartItemMapper;
import ruslan.shastkiv.bookstore.model.CartItem;
import ruslan.shastkiv.bookstore.model.ShoppingCart;
import ruslan.shastkiv.bookstore.repository.item.CartItemRepository;
import ruslan.shastkiv.bookstore.service.book.BookService;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final BookService bookService;
    private final CartItemMapper cartItemMapper;

    @Override
    public CartItem addCartItem(ShoppingCart shoppingCart, CartItemRequestDto requestDto) {
        Optional<CartItem> cartByBookId
                = cartItemRepository.findByBookId(requestDto.bookId());
        CartItem cartItem;
        if (cartByBookId.isPresent()) {
            cartItem = cartByBookId.get();
            cartItem.setQuantity(cartItem.getQuantity() + requestDto.quantity());
        } else {
            cartItem = initCartItem(shoppingCart, requestDto);
        }
        return cartItemRepository.save(cartItem);
    }

    @Override
    public CartItemDto updateItemQuantity(Long userId,
                                          Long cartItemId,
                                          UpdateCartItemRequestDto requestDto) {
        CartItem cartItem = getByCartAndUserId(userId, cartItemId);
        cartItem.setQuantity(requestDto.quantity());
        return cartItemMapper.toDto(cartItemRepository.save(cartItem));
    }

    @Override
    public void removeCartItem(Long userId, Long cartItemId) {
        CartItem cartItem = getByCartAndUserId(userId, cartItemId);
        cartItemRepository.delete(cartItem);
    }

    private CartItem initCartItem(ShoppingCart shoppingCart, CartItemRequestDto requestDto) {
        CartItem cartItem = new CartItem();
        cartItem.setBook(bookService.findBookById(requestDto.bookId()));
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setQuantity(requestDto.quantity());
        return cartItem;
    }

    private CartItem getByCartAndUserId(Long userId, Long cartItemId) {
        return cartItemRepository.findByIdAndShoppingCartId(cartItemId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cart item with ID [ " + cartItemId
                                + " ] or Shopping Cart for user ID [ " + userId + " ] not found"));
    }
}
