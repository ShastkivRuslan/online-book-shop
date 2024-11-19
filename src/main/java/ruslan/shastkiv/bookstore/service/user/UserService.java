package ruslan.shastkiv.bookstore.service.user;

import org.springframework.security.core.Authentication;
import ruslan.shastkiv.bookstore.dto.user.UserDto;
import ruslan.shastkiv.bookstore.dto.user.UserRegistrationRequestDto;
import ruslan.shastkiv.bookstore.model.User;

public interface UserService {
    UserDto register(UserRegistrationRequestDto registrationRequestDto);

    Long getUserId(Authentication authentication);

    User getUser(Authentication authentication);
}
