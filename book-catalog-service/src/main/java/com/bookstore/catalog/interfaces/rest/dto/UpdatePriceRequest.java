package com.bookstore.catalog.interfaces.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdatePriceRequest(@NotNull @Min(0) BigDecimal price) {}
