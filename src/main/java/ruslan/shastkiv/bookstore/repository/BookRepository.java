package ruslan.shastkiv.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ruslan.shastkiv.bookstore.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
}
