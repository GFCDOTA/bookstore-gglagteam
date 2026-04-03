package com.bookstore.catalog.interfaces.rest.dto;

import jakarta.validation.constraints.Min;

public record UpdateStockRequest(@Min(0) int quantity) {}
