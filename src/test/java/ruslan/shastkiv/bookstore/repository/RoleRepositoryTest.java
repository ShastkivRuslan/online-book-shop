package ruslan.shastkiv.bookstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ruslan.shastkiv.bookstore.model.Role;
import ruslan.shastkiv.bookstore.repository.role.RoleRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("""
            findByRoleName()
            - should return Optional with Role by role name
            """)
    public void findByRoleName_validRoleName_returnOptionalWithRole() {
        Optional<Role> actualRole = roleRepository.findByRoleName(Role.RoleName.ROLE_USER);

        assertTrue(actualRole.isPresent());
        assertEquals(Role.RoleName.ROLE_USER, actualRole.orElseThrow().getRoleName());
    }
}
