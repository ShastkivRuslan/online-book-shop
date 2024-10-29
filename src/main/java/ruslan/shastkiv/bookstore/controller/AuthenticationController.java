package ruslan.shastkiv.bookstore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ruslan.shastkiv.bookstore.dto.user.UserDto;
import ruslan.shastkiv.bookstore.dto.user.UserRegistrationRequestDto;
import ruslan.shastkiv.bookstore.exception.RegistrationException;
import ruslan.shastkiv.bookstore.service.user.UserService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;

    @PostMapping("/registration")
    public UserDto register(@RequestBody UserRegistrationRequestDto registrationRequestDto)
            throws RegistrationException {
        return userService.register(registrationRequestDto);
    }
}
