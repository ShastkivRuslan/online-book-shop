package ruslan.shastkiv.bookstore.service.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ruslan.shastkiv.bookstore.dto.book.BookDto;
import ruslan.shastkiv.bookstore.dto.book.BookSearchParametersDto;
import ruslan.shastkiv.bookstore.dto.book.CreateBookRequestDto;

import java.util.List;

public interface BookService {
    BookDto createBook(CreateBookRequestDto requestDto);

    Page<BookDto> getAll(Pageable pageable);

    BookDto getBookById(Long id);

    BookDto updateBook(Long id, CreateBookRequestDto bookDto);

    void deleteBook(Long id);

    Page<BookDto> search(BookSearchParametersDto searchParametersDto, Pageable pageable);

    List<BookDto> getAllBooksByCategoryId(Long id);
}

