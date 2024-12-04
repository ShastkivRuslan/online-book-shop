package ruslan.shastkiv.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.ONE_INVOCATION;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.ENCODED_PASSWORD;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.ROLE_USER_ID;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.USER_ID;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.createRole;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.createUser;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.createUserDto;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.createUserRegisterDto;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.getAuthentication;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import ruslan.shastkiv.bookstore.dto.user.UserDto;
import ruslan.shastkiv.bookstore.dto.user.UserRegistrationRequestDto;
import ruslan.shastkiv.bookstore.exception.RegistrationException;
import ruslan.shastkiv.bookstore.mapper.UserMapper;
import ruslan.shastkiv.bookstore.model.Role;
import ruslan.shastkiv.bookstore.model.User;
import ruslan.shastkiv.bookstore.repository.role.RoleRepository;
import ruslan.shastkiv.bookstore.repository.user.UserRepository;
import ruslan.shastkiv.bookstore.service.cart.ShoppingCartService;
import ruslan.shastkiv.bookstore.service.user.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private ShoppingCartService shoppingCartService;

    @Test
    @DisplayName("""
            register()
            - Should register a user with valid request
            """)
    public void register_validRequestDto_returnUserDto() {
        User user = createUser(USER_ID);
        UserDto expectedDto = createUserDto(USER_ID);
        UserRegistrationRequestDto userRegisterDto = createUserRegisterDto(USER_ID);
        Role role = createRole(ROLE_USER_ID);

        when(userRepository.existsByEmail(any(String.class))).thenReturn(false);
        when(userMapper.toModel(any(UserRegistrationRequestDto.class))).thenReturn(user);
        when(passwordEncoder.encode(any(String.class))).thenReturn(ENCODED_PASSWORD);
        when(roleRepository.findByRoleName(any(Role.RoleName.class))).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when((userMapper.toDto(any(User.class)))).thenReturn(expectedDto);

        UserDto actualDto = userService.register(userRegisterDto);
        verify(shoppingCartService, times(ONE_INVOCATION)).createShoppingCart(user);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("""
            register()
            - Should throw RegistrationException for an already registered email
            """)
    public void register_registeredEmail_throwsException() {
        UserRegistrationRequestDto userRegisterDto = createUserRegisterDto(USER_ID);
        when(userRepository.existsByEmail(any(String.class))).thenReturn(true);

        assertThrows(RegistrationException.class, ()
                -> userService.register(userRegisterDto));
    }

    @Test
    @DisplayName("""
            getUserId()
            - Should retrieve user ID from Authentication object
            """)
    public void getUserId_fromAuthentication_returnUserId() {
        User user = createUser(USER_ID);
        Authentication authentication = getAuthentication(user);

        Long actualId = userService.getUserId(authentication);

        assertEquals(USER_ID, actualId);
    }
}
