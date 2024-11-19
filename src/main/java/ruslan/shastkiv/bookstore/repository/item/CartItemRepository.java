package ruslan.shastkiv.bookstore.repository.item;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ruslan.shastkiv.bookstore.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByBookIdAndShoppingCartId(Long bookId, Long shoppingCartId);

    Optional<CartItem> findByIdAndShoppingCartId(Long id, Long cartId);

    @Query("SELECT c FROM CartItem c LEFT JOIN FETCH c.book WHERE c.shoppingCart.id = :id")
    Set<CartItem> findAllByShoppingCartId(Long id);
}
