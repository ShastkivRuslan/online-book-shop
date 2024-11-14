package ruslan.shastkiv.bookstore.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ruslan.shastkiv.bookstore.dto.cart.ShoppingCartDto;
import ruslan.shastkiv.bookstore.dto.item.CartItemDto;
import ruslan.shastkiv.bookstore.dto.item.CartItemRequestDto;
import ruslan.shastkiv.bookstore.dto.item.UpdateCartItemRequestDto;

@RestController
@RequestMapping("/cart")
public class ShoppingCartController {
    @GetMapping
    public Page<ShoppingCartDto> getShoppingCart(@ParameterObject @PageableDefault Pageable pageable) {
        return null;
        //todo
    }

    @PostMapping
    public CartItemDto addBookToCart(CartItemRequestDto requestDto) {
        return null;
        //todo
    }

    @PutMapping("/items/{cartItemId}")
    public CartItemDto updateQuantity(@PathVariable Long cartItemId, UpdateCartItemRequestDto requestDto) {
        return null;
        //todo
    }

    @DeleteMapping("/items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBookFromCart(@PathVariable Long cartItemId) {

    }
}
