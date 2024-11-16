package ruslan.shastkiv.bookstore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ruslan.shastkiv.bookstore.config.MapperConfig;
import ruslan.shastkiv.bookstore.dto.item.CartItemDto;
import ruslan.shastkiv.bookstore.dto.item.CartItemRequestDto;
import ruslan.shastkiv.bookstore.model.CartItem;

@Mapper(config = MapperConfig.class, uses = BookMapper.class)
public interface CartItemMapper {
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    CartItemDto toDto(CartItem cartItem);

    @Mapping(source = "bookId", target = "book", qualifiedByName = "bookFromId")
    CartItem toModel(CartItemRequestDto requestDto);

}
