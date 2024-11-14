package ruslan.shastkiv.bookstore.service.cart;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ruslan.shastkiv.bookstore.dto.cart.ShoppingCartDto;
import ruslan.shastkiv.bookstore.dto.item.CartItemDto;
import ruslan.shastkiv.bookstore.dto.item.CartItemRequestDto;
import ruslan.shastkiv.bookstore.exception.EntityNotFoundException;
import ruslan.shastkiv.bookstore.mapper.CartItemMapper;
import ruslan.shastkiv.bookstore.mapper.ShoppingCartMapper;
import ruslan.shastkiv.bookstore.model.ShoppingCart;
import ruslan.shastkiv.bookstore.model.User;
import ruslan.shastkiv.bookstore.repository.cart.ShoppingCartRepository;
import ruslan.shastkiv.bookstore.service.item.CartItemService;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemService cartItemService;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemMapper cartItemMapper;

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
    public CartItemDto addBookToCart(Long userId, CartItemRequestDto requestDto) {
        ShoppingCart shoppingCart = findShoppingCart(userId);
        return cartItemMapper.toDto(cartItemService.addCartItem(shoppingCart, requestDto));
    }

    @Override
    public ShoppingCart findShoppingCart(Long id) {
        return shoppingCartRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Cant find shopping cart by user id: " + id));
    }
}
