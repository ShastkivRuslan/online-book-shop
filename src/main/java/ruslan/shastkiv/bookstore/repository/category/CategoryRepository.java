package ruslan.shastkiv.bookstore.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;
import ruslan.shastkiv.bookstore.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
