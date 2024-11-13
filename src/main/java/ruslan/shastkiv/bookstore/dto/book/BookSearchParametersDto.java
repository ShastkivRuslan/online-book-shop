package ruslan.shastkiv.bookstore.dto.book;

public record BookSearchParametersDto(
        String[] authors,
        String[] titles,
        String[] prices,
        String[] categories) {
}
