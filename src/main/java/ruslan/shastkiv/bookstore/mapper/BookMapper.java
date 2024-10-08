package ruslan.shastkiv.bookstore.mapper;

import org.mapstruct.Mapper;
import ruslan.shastkiv.bookstore.config.MapperConfig;
import ruslan.shastkiv.bookstore.dto.BookDto;
import ruslan.shastkiv.bookstore.dto.CreateBookRequestDto;
import ruslan.shastkiv.bookstore.model.Book;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book employee);

    Book toModel(CreateBookRequestDto requestDto);
}

