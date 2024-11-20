package ruslan.shastkiv.bookstore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ruslan.shastkiv.bookstore.config.MapperConfig;
import ruslan.shastkiv.bookstore.dto.order.OrderDto;
import ruslan.shastkiv.bookstore.model.Order;
import ruslan.shastkiv.bookstore.model.ShoppingCart;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(target = "userId", source = "user.id")
    OrderDto toDto(Order order);

    Order toModel(OrderDto orderDto);

    @Mapping(target = "id", ignore = true)
    Order toOrder(ShoppingCart userShoppingCart);
}
