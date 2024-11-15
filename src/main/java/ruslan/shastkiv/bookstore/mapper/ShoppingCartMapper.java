package ruslan.shastkiv.bookstore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ruslan.shastkiv.bookstore.config.MapperConfig;
import ruslan.shastkiv.bookstore.dto.cart.ShoppingCartDto;
import ruslan.shastkiv.bookstore.model.ShoppingCart;

@Mapper(config = MapperConfig.class, uses = CartItemMapper.class)
public interface ShoppingCartMapper {
    @Mapping(target = "userId", source = "user.id")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);
}
