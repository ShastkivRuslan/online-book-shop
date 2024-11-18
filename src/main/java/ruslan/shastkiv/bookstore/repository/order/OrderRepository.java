package ruslan.shastkiv.bookstore.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import ruslan.shastkiv.bookstore.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
