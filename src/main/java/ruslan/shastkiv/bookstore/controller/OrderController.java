package ruslan.shastkiv.bookstore.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ruslan.shastkiv.bookstore.dto.order.OrderDto;
import ruslan.shastkiv.bookstore.dto.order.OrderItemDto;
import ruslan.shastkiv.bookstore.dto.order.PlaceOrderRequestDto;
import ruslan.shastkiv.bookstore.dto.order.UpdateOrderStatusRequestDto;
import ruslan.shastkiv.bookstore.service.order.OrderService;
import ruslan.shastkiv.bookstore.service.user.UserService;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto placeOrder(@RequestBody @Valid PlaceOrderRequestDto requestDto,
                               Authentication authentication) {
        return orderService.placeOrderByUserId(userService.getUserId(authentication));
    }

    @GetMapping
    public Page<OrderDto> getOrders (Authentication authentication,
                                     Pageable pageable) {
        return orderService.getAllOrdersByUserId(userService.getUserId(authentication), pageable);
    }

    @PatchMapping("/{orderId}")
    public OrderDto updateOrderStatus(@PathVariable Long orderId,
                                      @Valid UpdateOrderStatusRequestDto statusRequestDto) {
        return orderService.updateOrderStatus(orderId, statusRequestDto);
    }

    @GetMapping("/{orderId}/items")
    public Page<OrderItemDto> getOrderItems(@PathVariable Long orderId,
                                            Pageable pageable) {
        return orderService.getOrderItemsByOrderId(orderId, pageable);//todo
    }

    @GetMapping("/{orderId}/items/{itemId}")
    public OrderItemDto getOrderItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        return orderService.getOrderItem(orderId, itemId);//todo
    }
}
