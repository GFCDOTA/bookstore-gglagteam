package com.bookstore.catalog.interfaces.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateBookRequest(
        @NotBlank @Size(min = 2) String title,
        @NotBlank String author,
        @NotBlank String isbn,
        @NotNull @Min(0) BigDecimal price,
        @Min(0) int initialStock
) {}
