package ruslan.shastkiv.bookstore.repository.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ruslan.shastkiv.bookstore.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
