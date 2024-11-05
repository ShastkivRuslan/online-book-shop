package ruslan.shastkiv.bookstore.repository.role;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ruslan.shastkiv.bookstore.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(Role.RoleName roleName);
}
