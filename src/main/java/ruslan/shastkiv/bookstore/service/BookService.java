package ruslan.shastkiv.bookstore.service;

import java.util.List;
import ruslan.shastkiv.bookstore.dto.BookDto;
import ruslan.shastkiv.bookstore.dto.BookSearchParametersDto;
import ruslan.shastkiv.bookstore.dto.CreateBookRequestDto;

public interface BookService {
    BookDto createBook(CreateBookRequestDto requestDto);

    List<BookDto> getAll();

    BookDto getBookById(Long id);

    BookDto updateBook(Long id, CreateBookRequestDto bookDto);

    void deleteBook(Long id);

    List<BookDto> search(BookSearchParametersDto searchParametersDto);

}
