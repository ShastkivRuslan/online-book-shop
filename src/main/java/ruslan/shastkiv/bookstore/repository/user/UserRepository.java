package ruslan.shastkiv.bookstore.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ruslan.shastkiv.bookstore.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
