UPDATE books SET is_Deleted = FALSE WHERE id = 2;
INSERT INTO books_categories (book_id, category_id) VALUES (2, 2);
