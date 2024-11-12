package ruslan.shastkiv.bookstore.dto.category;

public record CategoryRequestDto(String name,
                                 String description) {
}
//todo: add validations for all fields