package ruslan.shastkiv.bookstore.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ruslan.shastkiv.bookstore.repository.user.UserRepository;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findUserWithRolesByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("Can`t find user by email ["
                        + email + "]")
        );
    }
}
