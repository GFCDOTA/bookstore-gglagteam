package com.bookstore.catalog.application.usecase;

import com.bookstore.catalog.domain.port.in.UpdateBookPriceUseCase;
import com.bookstore.catalog.domain.port.out.BookRepository;
import com.bookstore.catalog.domain.vo.BookId;
import com.bookstore.catalog.domain.vo.Money;
import com.bookstore.catalog.interfaces.exception.BookNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UpdateBookPriceUseCaseImpl implements UpdateBookPriceUseCase {

    private final BookRepository bookRepository;

    @Override
    public void execute(UUID bookId, BigDecimal newPrice) {
        var book = bookRepository.findById(BookId.of(bookId))
                .orElseThrow(() -> new BookNotFoundException("Book not found: " + bookId));

        book.updatePrice(Money.of(newPrice));
        bookRepository.save(book);

        log.info("Book price updated bookId={} newPrice={}", bookId, newPrice);
    }
}
