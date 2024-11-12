package ruslan.shastkiv.bookstore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ruslan.shastkiv.bookstore.config.MapperConfig;
import ruslan.shastkiv.bookstore.dto.book.BookDto;
import ruslan.shastkiv.bookstore.dto.book.BookDtoWithoutCategoryIds;
import ruslan.shastkiv.bookstore.dto.book.CreateBookRequestDto;
import ruslan.shastkiv.bookstore.model.Book;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);

    void updateBookFromDto(CreateBookRequestDto requestDto, @MappingTarget Book book);

    BookDtoWithoutCategoryIds toDtoWithoutCategoryIds (Book book);
}


