package ruslan.shastkiv.bookstore.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ruslan.shastkiv.bookstore.dto.user.UserDto;
import ruslan.shastkiv.bookstore.dto.user.UserRegistrationRequestDto;
import ruslan.shastkiv.bookstore.exception.RegistrationException;
import ruslan.shastkiv.bookstore.mapper.UserMapper;
import ruslan.shastkiv.bookstore.model.User;
import ruslan.shastkiv.bookstore.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto register(UserRegistrationRequestDto registrationRequestDto) {
        if (userRepository.existsByEmail(registrationRequestDto.email())) {
            throw new RegistrationException("User with email ["
                    + registrationRequestDto.email() + "] already registered!"
            );
        }
        User userFromRequest = userMapper.toModel(registrationRequestDto);
        userFromRequest.setPassword(passwordEncoder.encode(registrationRequestDto.password()));
        User savedUser = userRepository.save(userFromRequest);
        return userMapper.toDto(savedUser);
    }
}
