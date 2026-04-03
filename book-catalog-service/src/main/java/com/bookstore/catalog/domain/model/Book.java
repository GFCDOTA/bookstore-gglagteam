package com.bookstore.catalog.domain.model;

import com.bookstore.catalog.domain.vo.BookId;
import com.bookstore.catalog.domain.vo.ISBN;
import com.bookstore.catalog.domain.vo.Money;

public class Book {

    private final BookId id;
    private String title;
    private String author;
    private ISBN isbn;
    private Money price;
    private int stockQuantity;
    private BookStatus status;

    public static Book create(String title, String author, ISBN isbn, Money price, int initialStock) {
        if (title == null || title.length() < 2) {
            throw new IllegalArgumentException("Title must have at least 2 characters");
        }
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("Author cannot be blank");
        }
        if (price.amount().signum() < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (initialStock < 0) {
            throw new IllegalArgumentException("Initial stock cannot be negative");
        }

        var book = new Book(BookId.generate(), title, author, isbn, price, initialStock, BookStatus.ACTIVE);
        return book;
    }

    public Book(BookId id, String title, String author, ISBN isbn, Money price, int stockQuantity, BookStatus status) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.status = status;
    }

    public void updatePrice(Money newPrice) {
        if (newPrice.amount().signum() < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = newPrice;
    }

    public void updateStock(int newQuantity) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        this.stockQuantity = newQuantity;
    }

    public void reserveStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Reserve quantity must be positive");
        }
        if (quantity > stockQuantity) {
            throw new IllegalStateException(
                    "Insufficient stock for book " + id + ": requested=" + quantity + ", available=" + stockQuantity);
        }
        this.stockQuantity -= quantity;
    }

    public void releaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Release quantity must be positive");
        }
        this.stockQuantity += quantity;
    }

    public void deactivate() {
        this.status = BookStatus.INACTIVE;
    }

    public void activate() {
        this.status = BookStatus.ACTIVE;
    }

    // Getters
    public BookId id() { return id; }
    public String title() { return title; }
    public String author() { return author; }
    public ISBN isbn() { return isbn; }
    public Money price() { return price; }
    public int stockQuantity() { return stockQuantity; }
    public BookStatus status() { return status; }
}
