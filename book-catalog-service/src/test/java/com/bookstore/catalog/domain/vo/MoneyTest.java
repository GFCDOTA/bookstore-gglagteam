package com.bookstore.catalog.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    @DisplayName("should create Money with BigDecimal")
    void shouldCreateWithBigDecimal() {
        var money = Money.of(new BigDecimal("89.90"));
        assertEquals(new BigDecimal("89.90"), money.amount());
        assertEquals(Currency.getInstance("BRL"), money.currency());
    }

    @Test
    @DisplayName("should create Money with double")
    void shouldCreateWithDouble() {
        var money = Money.of(89.90);
        assertEquals(new BigDecimal("89.90"), money.amount());
    }

    @Test
    @DisplayName("should scale to 2 decimal places")
    void shouldScaleToTwoDecimals() {
        var money = Money.of(new BigDecimal("89.999"));
        assertEquals(new BigDecimal("90.00"), money.amount());
    }

    @Test
    @DisplayName("should multiply by quantity")
    void shouldMultiply() {
        var money = Money.of(10.00);
        var result = money.multiply(3);
        assertEquals(new BigDecimal("30.00"), result.amount());
    }

    @Test
    @DisplayName("should add two Money values")
    void shouldAdd() {
        var a = Money.of(10.00);
        var b = Money.of(20.50);
        var result = a.add(b);
        assertEquals(new BigDecimal("30.50"), result.amount());
    }

    @Test
    @DisplayName("should throw when adding different currencies")
    void shouldThrowWhenAddingDifferentCurrencies() {
        var brl = Money.of(10.00);
        var usd = new Money(new BigDecimal("10.00"), Currency.getInstance("USD"));

        assertThrows(IllegalArgumentException.class, () -> brl.add(usd));
    }

    @Test
    @DisplayName("should throw when amount is null")
    void shouldThrowWhenAmountIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Money(null, Currency.getInstance("BRL")));
    }

    @Test
    @DisplayName("should throw when currency is null")
    void shouldThrowWhenCurrencyIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Money(BigDecimal.TEN, null));
    }

    @Test
    @DisplayName("ZERO should have zero amount")
    void zeroShouldBeZero() {
        assertEquals(new BigDecimal("0.00"), Money.ZERO.amount());
    }
}
