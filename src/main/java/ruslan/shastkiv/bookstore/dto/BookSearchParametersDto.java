package ruslan.shastkiv.bookstore.dto;

public record BookSearchParametersDto(
        String[] authors,
        String[] titles,
        String[] prices) {
}
