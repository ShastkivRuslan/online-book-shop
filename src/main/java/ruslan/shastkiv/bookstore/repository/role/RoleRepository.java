package ruslan.shastkiv.bookstore.repository.role;

import org.springframework.data.jpa.repository.JpaRepository;
import ruslan.shastkiv.bookstore.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
