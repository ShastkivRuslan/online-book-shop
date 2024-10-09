package ruslan.shastkiv.bookstore.repository;

import java.util.List;
import java.util.Optional;
import ruslan.shastkiv.bookstore.model.Book;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();

    Optional<Book> findBookById(Long id);
}
