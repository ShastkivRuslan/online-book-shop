package ruslan.shastkiv.bookstore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ruslan.shastkiv.bookstore.config.MapperConfig;
import ruslan.shastkiv.bookstore.dto.book.BookDto;
import ruslan.shastkiv.bookstore.dto.book.BookDtoWithoutCategoryIds;
import ruslan.shastkiv.bookstore.dto.book.CreateBookRequestDto;
import ruslan.shastkiv.bookstore.model.Book;

@Mapper(config = MapperConfig.class, uses = CategoryMapper.class)
public interface BookMapper {
    @Mapping(target = "categories", source = "categories")
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);

    void updateBookFromDto(CreateBookRequestDto requestDto, @MappingTarget Book book);

    BookDtoWithoutCategoryIds toDtoWithoutCategoryIds(Book book);

    @Named("bookFromId")
    default Book bookFromId(Long bookId) {
        Book book = new Book();
        book.setId(bookId);
        return book;
    }
}


