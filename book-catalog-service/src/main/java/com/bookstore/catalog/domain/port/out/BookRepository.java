package com.bookstore.catalog.domain.port.out;

import com.bookstore.catalog.domain.model.Book;
import com.bookstore.catalog.domain.vo.BookId;
import com.bookstore.catalog.domain.vo.ISBN;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    Book save(Book book);
    Optional<Book> findById(BookId id);
    List<Book> findAll();
    boolean existsByIsbn(ISBN isbn);
}
