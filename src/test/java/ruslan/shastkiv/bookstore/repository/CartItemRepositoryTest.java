package ruslan.shastkiv.bookstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.CUSTOM_BOOK_TITLE;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.INVALID_BOOK_ID;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.THIRD_BOOK_ID;
import static ruslan.shastkiv.bookstore.utils.ShoppingCartTestUtils.INVALID_ITEM_ID;
import static ruslan.shastkiv.bookstore.utils.ShoppingCartTestUtils.ITEM_ID_3;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.INVALID_USER_ID;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.USER_ID;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ruslan.shastkiv.bookstore.model.CartItem;
import ruslan.shastkiv.bookstore.repository.item.CartItemRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(
        scripts = {
                "classpath:scripts/user/insert_user_to_db.sql",
                "classpath:scripts/user/insert_users_roles.sql",
                "classpath:scripts/cart/insert_shopping_cart.sql",
                "classpath:scripts/book/insert_books_to_db.sql",
                "classpath:scripts/cart/insert_cart_items.sql"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
@Sql(
        scripts = {
                "classpath:scripts/cart/remove_cart_items.sql",
                "classpath:scripts/book/remove_test_books_from_db.sql",
                "classpath:scripts/cart/remove_shopping_cart.sql",
                "classpath:scripts/user/remove_users_roles.sql",
                "classpath:scripts/user/remove_users.sql"

        },
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS
)
public class CartItemRepositoryTest {
    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    @DisplayName("""
            findByIdAndShoppingCartId()
            - should return Optional with CartItem when valid IDs are provided
            """)
    public void findByIdAndShoppingCartId_validIds_returnsOptionalWithCartItem() {

        Optional<CartItem> actual
                = cartItemRepository.findByIdAndShoppingCartId(ITEM_ID_3, USER_ID);
        String actualBookTitle = actual.orElseThrow().getBook().getTitle();
        Long actualCartId = actual.orElseThrow().getShoppingCart().getId();

        assertEquals(
                actualBookTitle,
                CUSTOM_BOOK_TITLE.formatted(THIRD_BOOK_ID)
        );
        assertEquals(USER_ID, actualCartId);
    }

    @Test
    @DisplayName("""
            findByIdAndShoppingCartId()
            - should return empty Optional when invalid Item ID is provided
            """)
    public void findByIdAndShoppingCartId_invalidItemId_returnsEmptyOptional() {
        Optional<CartItem> actual
                = cartItemRepository.findByIdAndShoppingCartId(INVALID_ITEM_ID, USER_ID);

        assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("""
            findByIdAndShoppingCartId()
            - should return empty Optional when invalid Cart ID is provided
            """)
    public void findByIdAndShoppingCartId_invalidCartId_returnsEmptyOptional() {
        Optional<CartItem> actual
                = cartItemRepository.findByIdAndShoppingCartId(ITEM_ID_3, INVALID_USER_ID);

        assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("""
            findByBookIdAndShoppingCartId()
            - should return Optional with CartItem when valid IDs are provided
            """)
    public void findByBookIdAndShoppingCartId_validIds_returnsOptionalWithCartItem() {
        Optional<CartItem> actual
                = cartItemRepository.findByBookIdAndShoppingCartId(THIRD_BOOK_ID, USER_ID);
        String actualBookTitle = actual.orElseThrow().getBook().getTitle();
        Long actualCartId = actual.orElseThrow().getShoppingCart().getId();

        assertEquals(
                actualBookTitle,
                CUSTOM_BOOK_TITLE.formatted(THIRD_BOOK_ID)
        );
        assertEquals(USER_ID, actualCartId);
    }

    @Test
    @DisplayName("""
            findByBookIdAndShoppingCartId()
            - should return empty Optional when invalid Book ID is provided
            """)
    public void findByBookIdAndShoppingCartId_invalidBookId_returnsEmptyOptional() {
        Optional<CartItem> actual
                = cartItemRepository.findByIdAndShoppingCartId(INVALID_BOOK_ID, USER_ID);

        assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("""
            findByBookIdAndShoppingCartId()
            - should return empty Optional when invalid Cart ID is provided
            """)
    public void findByBookIdAndShoppingCartId_invalidCartId_returnsEmptyOptional() {
        Optional<CartItem> actual
                = cartItemRepository.findByIdAndShoppingCartId(THIRD_BOOK_ID, INVALID_USER_ID);

        assertTrue(actual.isEmpty());
    }
}
