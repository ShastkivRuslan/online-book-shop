package ruslan.shastkiv.bookstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.CUSTOM_EMAIL;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.INVALID_USER_ID;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.USER_ID;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ruslan.shastkiv.bookstore.model.User;
import ruslan.shastkiv.bookstore.repository.user.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(
        scripts = {
                "classpath:scripts/user/insert_user_to_db.sql",
                "classpath:scripts/user/insert_users_roles.sql"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
@Sql(
        scripts = {
                "classpath:scripts/user/remove_users_roles.sql",
                "classpath:scripts/user/remove_users.sql"
        },
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS
)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("""
            existsByEmail()
            - should return true for existing email
            """)
    public void existsByEmail_validEmail_returnTrue() {
        assertTrue(userRepository.existsByEmail(CUSTOM_EMAIL.formatted(USER_ID)));
    }

    @Test
    @DisplayName("""
            existsByEmail()
            - should return false for non-existing email
            """)
    public void existsByEmail_notExistedEmail_returnFalse() {
        assertFalse(userRepository.existsByEmail(CUSTOM_EMAIL.formatted(INVALID_USER_ID)));
    }

    @Test
    @DisplayName("""
            findByEmail()
            - should return user for valid email
            """)
    public void findByEmail_validEmail_returnOptionalWithUser() {
        Optional<User> actualUser = userRepository.findByEmail(CUSTOM_EMAIL.formatted(USER_ID));

        assertTrue(actualUser.isPresent());
        assertEquals(CUSTOM_EMAIL.formatted(USER_ID), actualUser.get().getEmail());
    }

    @Test
    @DisplayName("""
            findByEmail()
            - should return empty optional for non-existing email
            """)
    public void findByEmail_invalidEmail_returnEmptyOptional() {
        Optional<User> actualUser
                = userRepository.findByEmail(CUSTOM_EMAIL.formatted(INVALID_USER_ID));

        assertTrue(actualUser.isEmpty());
    }
}
