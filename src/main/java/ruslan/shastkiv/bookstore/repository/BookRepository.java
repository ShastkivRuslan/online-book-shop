package ruslan.shastkiv.bookstore.repository;

import java.util.List;
import ruslan.shastkiv.bookstore.model.Book;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();
}
