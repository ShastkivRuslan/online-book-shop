package ruslan.shastkiv.bookstore.utils;

import java.util.ArrayList;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ruslan.shastkiv.bookstore.dto.user.UserDto;
import ruslan.shastkiv.bookstore.dto.user.UserLoginRequestDto;
import ruslan.shastkiv.bookstore.dto.user.UserRegistrationRequestDto;
import ruslan.shastkiv.bookstore.model.User;

public class UserTestUtils {
    public static final Long ADMIN_ID = 2L;
    public static final Long USER_ID = 3L;
    public static final Long NEW_USER_ID = 4L;

    public static final String AUTH_URL = "/auth";
    public static final String REGISTER_URL = "/registration";
    public static final String LOGIN_URL = "/login";

    public static final String CUSTOM_FIRST_NAME = "First_name_%s";
    public static final String CUSTOM_LAST_NAME = "Last_name_%s";
    public static final String CUSTOM_EMAIL = "user_email_%s@mail.com";
    public static final String CUSTOM_PASSWORD = "Password_%s";
    public static final String CUSTOM_SHIPPING_ADDRESS = "Shipping_address_%s";

    public static User createUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setEmail(CUSTOM_EMAIL.formatted(id));
        user.setPassword(CUSTOM_PASSWORD.formatted(id));
        user.setFirstName(CUSTOM_FIRST_NAME.formatted(id));
        user.setLastName(CUSTOM_LAST_NAME.formatted(id));
        user.setShippingAddress(CUSTOM_SHIPPING_ADDRESS.formatted(id));
        return user;
    }

    public static UserLoginRequestDto createUserLoginRequestDto(Long id) {
        return new UserLoginRequestDto(
                CUSTOM_EMAIL.formatted(id),
                CUSTOM_PASSWORD.formatted(id.intValue())
        );
    }

    public static UserRegistrationRequestDto createUserRegisterDto(Long id) {
        return new UserRegistrationRequestDto(
                CUSTOM_EMAIL.formatted(id),
                CUSTOM_PASSWORD.formatted(id),
                CUSTOM_PASSWORD.formatted(id),
                CUSTOM_FIRST_NAME.formatted(id),
                CUSTOM_LAST_NAME.formatted(id),
                CUSTOM_SHIPPING_ADDRESS.formatted(id)
        );
    }

    public static UserDto createUserDto(Long id) {
        return new UserDto(
                id,
                CUSTOM_EMAIL.formatted(id),
                CUSTOM_FIRST_NAME.formatted(id),
                CUSTOM_LAST_NAME.formatted(id),
                CUSTOM_SHIPPING_ADDRESS.formatted(id)
        );
    }

    public static Authentication getAuthentication(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(
                user, user.getPassword(), authorities);
    }
}
