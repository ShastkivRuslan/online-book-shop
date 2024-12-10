package ruslan.shastkiv.bookstore.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.PAGEABLE;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.INVALID_USER_ID;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.USER_ID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.jdbc.Sql;
import ruslan.shastkiv.bookstore.model.Order;
import ruslan.shastkiv.bookstore.repository.order.OrderRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(
        scripts = {
                "classpath:scripts/user/insert_user_to_db.sql",
                "classpath:scripts/user/insert_users_roles.sql",
                "classpath:scripts/cart/insert_shopping_cart.sql",
                "classpath:scripts/book/insert_books_to_db.sql",
                "classpath:scripts/order/insert_order.sql",
                "classpath:scripts/order/insert_order_items.sql"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
@Sql(
        scripts = {
                "classpath:scripts/order/remove_order_items.sql",
                "classpath:scripts/order/remove_order.sql",
                "classpath:scripts/book/remove_test_books_from_db.sql",
                "classpath:scripts/cart/remove_shopping_cart.sql",
                "classpath:scripts/user/remove_users_roles.sql",
                "classpath:scripts/user/remove_users.sql"
        },
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS
)
public class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("""
            findAllByUserId()
            - should return pageable orders for existing user
            """)
    public void findAllByUserId_existedUserId_returnsPageableOrders() {
        Page<Order> actualPage = orderRepository.findAllByUserId(USER_ID, PAGEABLE);

        assertFalse(actualPage.getContent().isEmpty());
    }

    @Test
    @DisplayName("""
            findAllByUserId()
            - should return empty page for user without orders
            """)
    public void findAllByUserId_userIdWithoutOrders_returnsEmptyPage() {
        Page<Order> actualPage = orderRepository.findAllByUserId(INVALID_USER_ID, PAGEABLE);

        assertTrue(actualPage.getContent().isEmpty());
    }
}
