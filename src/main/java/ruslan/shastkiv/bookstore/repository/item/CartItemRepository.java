package ruslan.shastkiv.bookstore.repository.item;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ruslan.shastkiv.bookstore.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByBookIdAndShoppingCartId(Long bookId, Long shoppingCartId);

    Optional<CartItem> findByIdAndShoppingCartId(Long id, Long cartId);
}
