package pl.multiplex.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object reprezentujący kwotę i walutę.
 * Prosty wariant: trzymamy amount w skali 2 i walutę jako String.
 */
public record Money(BigDecimal amount, String currency) {

    public Money {
        amount = Objects.requireNonNull(amount, "amount cannot be null")
                .setScale(2, RoundingMode.HALF_UP);

        currency = Objects.requireNonNull(currency, "currency cannot be null").trim().toUpperCase();

        if (currency.isBlank()) {
            throw new IllegalArgumentException("currency cannot be blank");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }
    }

    public static Money pln(double value) {
        return new Money(BigDecimal.valueOf(value), "PLN");
    }

    public Money plus(Money other) {
        ensureSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money times(int multiplier) {
        if (multiplier < 0) throw new IllegalArgumentException("multiplier cannot be negative");
        return new Money(this.amount.multiply(BigDecimal.valueOf(multiplier)), this.currency);
    }

    private void ensureSameCurrency(Money other) {
        Objects.requireNonNull(other, "other cannot be null");
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch: " + this.currency + " vs " + other.currency);
        }
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }
}
