package ruslan.shastkiv.bookstore.dto.user;

public record UserDto(Long id,
                      String email,
                      String firstName,
                      String lastName,
                      String shippingAddress) {
}
