package ruslan.shastkiv.bookstore.service.book;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ruslan.shastkiv.bookstore.dto.book.BookDto;
import ruslan.shastkiv.bookstore.dto.book.BookDtoWithoutCategoryIds;
import ruslan.shastkiv.bookstore.dto.book.BookSearchParametersDto;
import ruslan.shastkiv.bookstore.dto.book.CreateBookRequestDto;
import ruslan.shastkiv.bookstore.exception.EntityNotFoundException;
import ruslan.shastkiv.bookstore.mapper.BookMapper;
import ruslan.shastkiv.bookstore.model.Book;
import ruslan.shastkiv.bookstore.model.Category;
import ruslan.shastkiv.bookstore.repository.book.BookRepository;
import ruslan.shastkiv.bookstore.repository.book.BookSpecificationBuilder;
import ruslan.shastkiv.bookstore.repository.category.CategoryRepository;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto createBook(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        book.setCategories(initCategories(requestDto.getCategoryIds()));
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public Page<BookDto> getAll(Pageable pageable) {
        return bookRepository.findAllWithCategories(pageable)
                .map(bookMapper::toDto);
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = findBookById(id);
        return bookMapper.toDto(book);
    }

    @Override
    public BookDto updateBook(Long id, CreateBookRequestDto requestDto) {
        Book book = findBookById(id);
        bookMapper.updateBookFromDto(requestDto, book);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public Page<BookDto> search(BookSearchParametersDto searchParametersDto, Pageable pageable) {
        return bookRepository.findAll(bookSpecificationBuilder.build(searchParametersDto), pageable)
                .map(bookMapper::toDto);
    }

    @Override
    public Page<BookDtoWithoutCategoryIds> getAllBooksByCategoryId(Long id, Pageable pageable) {
        return bookRepository.findBooksByCategoryId(id, pageable)
                .map(bookMapper::toDtoWithoutCategoryIds);
    }

    @Override
    public Book findBookById(Long id) {
        return bookRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Can t find book by id: " + id));
    }

    private Set<Category> initCategories(Set<Long> categoryIds) {
        List<Category> categoriesByIds = categoryRepository.findAllById(categoryIds);

        Set<Long> categoryIdsInDB = categoriesByIds.stream()
                .map(Category::getId)
                .collect(Collectors.toSet());

        for (Long id : categoryIds) {
            if (!categoryIdsInDB.contains(id)) {
                throw new EntityNotFoundException("Can`t find category by id: " + id);
            }
        }
        return new HashSet<>(categoriesByIds);
    }
}
