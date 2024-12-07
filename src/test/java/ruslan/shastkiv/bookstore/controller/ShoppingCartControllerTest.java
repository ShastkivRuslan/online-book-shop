package ruslan.shastkiv.bookstore.controller;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.SECOND_BOOK_ID;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.THIRD_BOOK_ID;
import static ruslan.shastkiv.bookstore.utils.ShoppingCartTestUtils.ITEM_ID_3;
import static ruslan.shastkiv.bookstore.utils.ShoppingCartTestUtils.ITEM_ID_FOR_DELETE;
import static ruslan.shastkiv.bookstore.utils.ShoppingCartTestUtils.ITEM_URL;
import static ruslan.shastkiv.bookstore.utils.ShoppingCartTestUtils.SHOPPING_CART_URL;
import static ruslan.shastkiv.bookstore.utils.ShoppingCartTestUtils.UPDATED_QUANTITY;
import static ruslan.shastkiv.bookstore.utils.ShoppingCartTestUtils.createCartItemRequestDto;
import static ruslan.shastkiv.bookstore.utils.ShoppingCartTestUtils.createShoppingCartDto;
import static ruslan.shastkiv.bookstore.utils.ShoppingCartTestUtils.createUpdateCartItemDto;
import static ruslan.shastkiv.bookstore.utils.ShoppingCartTestUtils.createUpdatedShoppingCartDto;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.USER_ID;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.createUser;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.getAuthentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ruslan.shastkiv.bookstore.dto.cart.ShoppingCartDto;
import ruslan.shastkiv.bookstore.dto.item.CartItemRequestDto;
import ruslan.shastkiv.bookstore.dto.item.UpdateCartItemRequestDto;
import ruslan.shastkiv.bookstore.model.User;

@Sql(
        scripts = {
                "classpath:scripts/user/insert_user_to_db.sql",
                "classpath:scripts/user/insert_users_roles.sql",
                "classpath:scripts/cart/insert_shopping_cart.sql",
                "classpath:scripts/book/insert_books_to_db.sql",
                "classpath:scripts/cart/insert_cart_items.sql"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
@Sql(
        scripts = {
                "classpath:scripts/cart/remove_cart_items.sql",
                "classpath:scripts/book/remove_test_books_from_db.sql",
                "classpath:scripts/cart/remove_shopping_cart.sql",
                "classpath:scripts/user/remove_users_roles.sql",
                "classpath:scripts/user/remove_users.sql"
        },
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS
)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShoppingCartControllerTest {
    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("""
            getShoppingCart()
            - Should return a shopping cart DTO
            """)
    @WithMockUser(username = "user")
    public void getShoppingCart_validUser_returnShoppingCartDto() throws Exception {
        User user = createUser(USER_ID);
        ShoppingCartDto expectedDto = createShoppingCartDto(USER_ID, List.of(THIRD_BOOK_ID));
        Authentication authentication = getAuthentication(user);

        MvcResult result = mockMvc.perform(get(SHOPPING_CART_URL)
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto actualDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("""
            addBook()
            - Should add a book to shopping cart and return a shopping cart DTO
            """)
    @WithMockUser(username = "user")
    @Sql(
            scripts = "classpath:scripts/cart/remove_cart_item_after_create.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void addBook_validBook_returnShoppingCartDto() throws Exception {
        User user = createUser(USER_ID);
        Authentication authentication = getAuthentication(user);
        CartItemRequestDto cartItemRequestDto = createCartItemRequestDto(SECOND_BOOK_ID);
        String json = objectMapper.writeValueAsString(cartItemRequestDto);
        ShoppingCartDto expectedDto
                = createShoppingCartDto(USER_ID, List.of(SECOND_BOOK_ID, THIRD_BOOK_ID));

        MvcResult result = mockMvc.perform(post(SHOPPING_CART_URL)
                        .content(json)
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        ShoppingCartDto actualDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class);

        assertTrue(expectedDto.cartItems().stream()
                .allMatch(expectedItemDto -> actualDto.cartItems().stream()
                        .anyMatch(actualItemDto
                                -> reflectionEquals(expectedItemDto, actualItemDto, "id"))));
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("""
            updateQuantity()
            - Should update quantity of books and return updated shopping cart Dto
            """)
    @Sql(
            scripts = "classpath:scripts/cart/revert_updated_quantity.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void updateQuantity_existedCartItem_returnUpdatedShoppingCartDto() throws Exception {
        User user = createUser(USER_ID);
        Authentication authentication = getAuthentication(user);
        UpdateCartItemRequestDto updateCartItemDro = createUpdateCartItemDto(UPDATED_QUANTITY);
        String json = objectMapper.writeValueAsString(updateCartItemDro);
        ShoppingCartDto expectedDto
                = createUpdatedShoppingCartDto(USER_ID, ITEM_ID_3, UPDATED_QUANTITY);

        MvcResult result = mockMvc.perform(put(ITEM_URL + USER_ID.intValue())
                        .with(authentication(authentication))
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto actualDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("""
            removeBookFromCart()
            - Should delete book from shopping cart and return no content status
            """)
    @WithMockUser(username = "user")
    @Sql(
            scripts = "classpath:scripts/cart/insert_cart_items.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void removeBookFromCart_validCartItemId_returnNoContent() throws Exception {
        User user = createUser(USER_ID);
        Authentication authentication = getAuthentication(user);

        mockMvc.perform(delete(ITEM_URL + ITEM_ID_FOR_DELETE)
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }

}
