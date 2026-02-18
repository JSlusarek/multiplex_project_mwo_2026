package pl.multiplex.shared;

import java.util.Objects;

/**
 * Value Object identyfikujący miejsce w sali: rząd + numer.
 */
public record SeatId(String row, int number) {

    public SeatId {
        row = Objects.requireNonNull(row, "row cannot be null").trim().toUpperCase();

        if (row.isBlank()) {
            throw new IllegalArgumentException("row cannot be blank");
        }
        if (number <= 0) {
            throw new IllegalArgumentException("number must be > 0");
        }
    }

    @Override
    public String toString() {
        return row + number; // np. H34
    }
}
