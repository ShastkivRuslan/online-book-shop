package ruslan.shastkiv.bookstore.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ruslan.shastkiv.bookstore.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}
