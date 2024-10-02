package ruslan.shastkiv.bookstore.service;

import java.util.List;
import ruslan.shastkiv.bookstore.model.Book;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();
}
