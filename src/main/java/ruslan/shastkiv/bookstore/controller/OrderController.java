package ruslan.shastkiv.bookstore.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto placeOrder(@RequestBody @Valid PlaceOrderrequestDto placeOrderrequestDto) {
        return null;//todo
    }

    @GetMapping
    public Page<OrderDto> getOrders (Authentication authentication,
                                     Pageable pageable) {
        return null;//todo
    }

    @PatchMapping("/{orderId}")
    public OrderDto updateOrderStatus(@PathVariable Long orderId,
                                      @Valid UpdateStatusRequestDto requestDto) {
        return null;//todo
    }

    @GetMapping("/{orderId}/items")
    public Page<OrderItemDto> getOrderItems(@PathVariable Long orderId,
                                      Pageable pageable) {
        return null;//todo
    }

    @GetMapping("/{orderId}/items/{itemId}")
    public OrderItemDto getOrderItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        return null;//todo
    }
}
