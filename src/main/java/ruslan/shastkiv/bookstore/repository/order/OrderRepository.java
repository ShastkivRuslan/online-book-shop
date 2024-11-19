package ruslan.shastkiv.bookstore.repository.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ruslan.shastkiv.bookstore.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAllByUserId(Long userId, Pageable pageable);
}
