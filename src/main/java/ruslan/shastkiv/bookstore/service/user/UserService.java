package ruslan.shastkiv.bookstore.service.user;

import ruslan.shastkiv.bookstore.dto.user.UserDto;
import ruslan.shastkiv.bookstore.dto.user.UserRegistrationRequestDto;

public interface UserService {
    UserDto register(UserRegistrationRequestDto registrationRequestDto);
}
