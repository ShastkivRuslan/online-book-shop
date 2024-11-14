package ruslan.shastkiv.bookstore.repository.item;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ruslan.shastkiv.bookstore.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByBookId(Long bookId);

    Optional<CartItem> findByIdAndShoppingCartId(Long id, Long cartId);
}
