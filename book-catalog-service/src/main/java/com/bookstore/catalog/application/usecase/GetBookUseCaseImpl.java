package com.bookstore.catalog.application.usecase;

import com.bookstore.catalog.domain.port.in.GetBookUseCase;
import com.bookstore.catalog.domain.port.out.BookRepository;
import com.bookstore.catalog.domain.vo.BookId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetBookUseCaseImpl implements GetBookUseCase {

    private final BookRepository bookRepository;

    @Override
    public Optional<BookView> findById(UUID id) {
        return bookRepository.findById(BookId.of(id))
                .map(book -> new BookView(
                        book.id().value(),
                        book.title(),
                        book.author(),
                        book.isbn().value(),
                        book.price().amount(),
                        book.stockQuantity(),
                        book.status().name()
                ));
    }

    @Override
    public List<BookView> findAll() {
        return bookRepository.findAll().stream()
                .map(book -> new BookView(
                        book.id().value(),
                        book.title(),
                        book.author(),
                        book.isbn().value(),
                        book.price().amount(),
                        book.stockQuantity(),
                        book.status().name()
                ))
                .toList();
    }
}
