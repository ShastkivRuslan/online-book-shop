package ruslan.shastkiv.bookstore.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ruslan.shastkiv.bookstore.model.Book;
import ruslan.shastkiv.bookstore.repository.BookRepository;
import ruslan.shastkiv.bookstore.service.BookService;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }
}
