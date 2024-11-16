package ruslan.shastkiv.bookstore.repository.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import ruslan.shastkiv.bookstore.model.ShoppingCart;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
}
