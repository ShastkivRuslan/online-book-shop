package ruslan.shastkiv.bookstore.service.cart;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import ruslan.shastkiv.bookstore.model.ShoppingCart;
import ruslan.shastkiv.bookstore.model.User;
import ruslan.shastkiv.bookstore.repository.cart.ShoppingCartRepository;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    public void createShoppingCart(User user) {
        ShoppingCart cart = new ShoppingCart();
        cart.setUser(user);
        shoppingCartRepository.save(cart);
    }
}
