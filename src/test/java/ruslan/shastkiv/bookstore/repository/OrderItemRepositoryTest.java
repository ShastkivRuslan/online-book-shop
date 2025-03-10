package ruslan.shastkiv.bookstore.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.CUSTOM_BOOK_TITLE;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.FIRST_BOOK_ID;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.PAGEABLE;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.SECOND_BOOK_ID;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.NOT_EXISTED_ORDER_ID;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.NOT_EXISTED_ORDER_ITEM_ID;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.ORDER_ID_1;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.ORDER_ITEM_ID_1;
import static ruslan.shastkiv.bookstore.utils.OrderTestUtils.createOrder;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.USER_ID;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.createUser;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.jdbc.Sql;
import ruslan.shastkiv.bookstore.model.Order;
import ruslan.shastkiv.bookstore.model.OrderItem;
import ruslan.shastkiv.bookstore.model.User;
import ruslan.shastkiv.bookstore.repository.order.OrderItemRepository;

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
public class OrderItemRepositoryTest {
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    @DisplayName("""
            findAllByOrderId()
            - should return pageable Order Items when valid Order ID is provided
            """)
    public void findAllByOrderId_validOrderId_returnPageableOrderItems() {
        User user = createUser(USER_ID);
        Order expectedOrder = createOrder(user, List.of(FIRST_BOOK_ID, SECOND_BOOK_ID));
        Set<OrderItem> expectedItems = expectedOrder.getOrderItems();

        Page<OrderItem> actualPage = orderItemRepository.findAllByOrderId(ORDER_ID_1, PAGEABLE);
        Set<OrderItem> actualItems = actualPage.get().collect(Collectors.toSet());

        assertTrue(actualPage.hasContent());
        assertAll("Order items lists comparison",
                () -> assertIterableEquals(
                        expectedItems.stream().map(OrderItem::getId).collect(Collectors.toSet()),
                        actualItems.stream().map(OrderItem::getId).collect(Collectors.toSet()),
                        "IDs do not match"
                ),
                () -> assertIterableEquals(
                        expectedItems.stream().map(OrderItem::getBook).collect(Collectors.toSet()),
                        actualItems.stream().map(OrderItem::getBook).collect(Collectors.toSet()),
                        "Books do not match"
                ),
                () -> assertIterableEquals(
                        expectedItems.stream().map(OrderItem::getQuantity)
                                .collect(Collectors.toSet()),
                        actualItems.stream().map(OrderItem::getQuantity)
                                .collect(Collectors.toSet()),
                        "Quantities do not match"
                ),
                () -> assertTrue(
                        actualItems.stream().allMatch(item
                                -> item.getOrder().getId().equals(ORDER_ID_1)),
                        "Order Ids do not match"
                )
        );
    }

    @Test
    @DisplayName("""
            findAllByOrderId()
            - should return empty pageable Order Items when invalid Order ID is provided
            """)
    public void findAllByOrderId_notExistedOrderId_returnEmptyPage() {
        Page<OrderItem> actualPage
                = orderItemRepository.findAllByOrderId(NOT_EXISTED_ORDER_ID, PAGEABLE);
        assertTrue(actualPage.isEmpty());
    }

    @Test
    @DisplayName("""
            findByIdAndOrderId()
            - should return Optional with Order Item when valid IDs are provided
            """)
    public void findByIdAndOrderId_validRequest_returnOptionalWithOrderItem() {
        Optional<OrderItem> actual
                = orderItemRepository.findByIdAndOrderId(ORDER_ITEM_ID_1, ORDER_ID_1);

        assertTrue(actual.isPresent());
        assertEquals(
                actual.get().getBook().getTitle(),
                CUSTOM_BOOK_TITLE.formatted(FIRST_BOOK_ID)
        );
    }

    @Test
    @DisplayName("""
            findByIdAndOrderId()
            - should return empty Optional when invalid Order ID is provided
            """)
    public void findByIdAndOrderId_invalidOrderId_returnEmptyOptional() {
        Optional<OrderItem> actual
                = orderItemRepository.findByIdAndOrderId(ORDER_ITEM_ID_1, NOT_EXISTED_ORDER_ID);

        assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("""
            findByIdAndOrderId()
            - should return empty Optional when invalid Order Item ID is provided
            """)
    public void findByIdAndOrderId_invalidOrderItemId_returnEmptyOptional() {
        Optional<OrderItem> actual
                = orderItemRepository.findByIdAndOrderId(NOT_EXISTED_ORDER_ITEM_ID, ORDER_ID_1);

        assertTrue(actual.isEmpty());
    }
}
