package ruslan.shastkiv.bookstore.service.user;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ruslan.shastkiv.bookstore.dto.user.UserDto;
import ruslan.shastkiv.bookstore.dto.user.UserRegistrationRequestDto;
import ruslan.shastkiv.bookstore.exception.EntityNotFoundException;
import ruslan.shastkiv.bookstore.exception.RegistrationException;
import ruslan.shastkiv.bookstore.mapper.UserMapper;
import ruslan.shastkiv.bookstore.model.Role;
import ruslan.shastkiv.bookstore.model.User;
import ruslan.shastkiv.bookstore.repository.role.RoleRepository;
import ruslan.shastkiv.bookstore.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserDto register(UserRegistrationRequestDto registrationRequestDto) {
        if (userRepository.existsByEmail(registrationRequestDto.email())) {
            throw new RegistrationException("User with email ["
                    + registrationRequestDto.email() + "] already registered!"
            );
        }

        User user = userMapper.toModel(registrationRequestDto);
        user.setPassword(passwordEncoder.encode(registrationRequestDto.password()));
        user.setRoles(Set.of(roleRepository.findByRoleName(Role.RoleName.ROLE_USER)
                .orElseThrow(() -> new EntityNotFoundException("Role not found!"))));
        userRepository.save(user);
        return userMapper.toDto(user);
    }
}
