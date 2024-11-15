package ruslan.shastkiv.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ruslan.shastkiv.bookstore.dto.cart.ShoppingCartDto;
import ruslan.shastkiv.bookstore.dto.item.CartItemDto;
import ruslan.shastkiv.bookstore.dto.item.CartItemRequestDto;
import ruslan.shastkiv.bookstore.dto.item.UpdateCartItemRequestDto;
import ruslan.shastkiv.bookstore.service.cart.ShoppingCartService;
import ruslan.shastkiv.bookstore.service.item.CartItemService;
import ruslan.shastkiv.bookstore.service.user.UserService;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Tag(name = "Shopping Cart", description = "Endpoints for managing the shopping cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    private final CartItemService cartItemService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get the shopping cart of the current user")
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        return shoppingCartService.getShoppingCart(userService.getUserId(authentication));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a book to the shopping cart")
    public CartItemDto addBook(
            Authentication authentication,
            @RequestBody @Valid CartItemRequestDto requestDto) {
        return shoppingCartService.addBookToCart(
                userService.getUserId(authentication), requestDto);
    }

    @PutMapping("/items/{cartItemId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update quantity of a cart item")
    public CartItemDto updateQuantity(
            @PathVariable Long cartItemId,
            Authentication authentication,
            @RequestBody @Valid UpdateCartItemRequestDto requestDto) {
        return cartItemService.updateItemQuantity(
                userService.getUserId(authentication), cartItemId, requestDto);
    }

    @DeleteMapping("/items/{cartItemId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Remove a book from the shopping cart")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBookFromCart(@PathVariable Long cartItemId, Authentication authentication) {
        cartItemService.removeCartItem(userService.getUserId(authentication), cartItemId);
    }
}
