package ruslan.shastkiv.bookstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.AUTH_URL;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.LOGIN_URL;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.NEW_USER_ID;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.REGISTER_URL;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.USER_ID;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.createUserDto;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.createUserLoginRequestDto;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.createUserRegisterDto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ruslan.shastkiv.bookstore.dto.user.UserDto;
import ruslan.shastkiv.bookstore.dto.user.UserLoginRequestDto;
import ruslan.shastkiv.bookstore.dto.user.UserLoginResponseDto;
import ruslan.shastkiv.bookstore.dto.user.UserRegistrationRequestDto;

@Sql(
        scripts = {
                "classpath:scripts/user/insert_user_to_db.sql",
                "classpath:scripts/user/insert_users_roles.sql"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
@Sql(
        scripts = {
                "classpath:scripts/user/remove_users_roles.sql",
                "classpath:scripts/user/remove_users.sql"
        },
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS
)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerTest {
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
            register()
            - Should register new user and return user dto
            """)
    @Sql(
            scripts = {
                    "classpath:scripts/user/remove_roles_registered_user.sql",
                    "classpath:scripts/user/remove_user_after_registration.sql"
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void register_validRequestDto_returnUserDto() throws Exception {
        UserRegistrationRequestDto userRegisterDto = createUserRegisterDto(NEW_USER_ID);
        String json = objectMapper.writeValueAsString(userRegisterDto);
        UserDto expectedDto = createUserDto(NEW_USER_ID);

        MvcResult result = mockMvc.perform(post(AUTH_URL + REGISTER_URL)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserDto actualDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserDto.class);
        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("""
            login()
            - Should login user with valid loginDto and return token
            """)
    @Sql(
            scripts = "classpath:scripts/user/set_encoded_password.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    public void login_validRequestDto_returnUserLoginResponseDto() throws Exception {
        UserLoginRequestDto userLoginRequestDto = createUserLoginRequestDto(USER_ID);
        String json = objectMapper.writeValueAsString(userLoginRequestDto);

        MvcResult result = mockMvc.perform(
                        post(AUTH_URL + LOGIN_URL)
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        UserLoginResponseDto actualDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserLoginResponseDto.class);

        assertFalse(actualDto.token().isEmpty());
    }
}
