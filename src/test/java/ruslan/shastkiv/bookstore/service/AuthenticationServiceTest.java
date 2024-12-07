package ruslan.shastkiv.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.JWT_TOKEN_EXAMPLE;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.USER_ID;
import static ruslan.shastkiv.bookstore.utils.UserTestUtils.createUserLoginRequestDto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import ruslan.shastkiv.bookstore.dto.user.UserLoginRequestDto;
import ruslan.shastkiv.bookstore.dto.user.UserLoginResponseDto;
import ruslan.shastkiv.bookstore.security.AuthenticationService;
import ruslan.shastkiv.bookstore.security.JwtUtil;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {
    @InjectMocks
    private AuthenticationService authenticationService;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AuthenticationManager authenticationManager;

    @Test
    @DisplayName("""
            authenticate()
            - should return a token when provided valid credentials
            """)
    public void authenticate_validRequest_returnToken() {
        UserLoginRequestDto requestDto = createUserLoginRequestDto(USER_ID);

        when(jwtUtil.generateToken(eq(requestDto.email()))).thenReturn(JWT_TOKEN_EXAMPLE);
        UserLoginResponseDto responseDto = authenticationService.authenticate(requestDto);

        assertEquals(JWT_TOKEN_EXAMPLE, responseDto.token());
        verify(jwtUtil).generateToken(requestDto.email());
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.email(), requestDto.password())
        );
    }
}
