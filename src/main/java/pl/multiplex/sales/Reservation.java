package pl.multiplex.sales;

import pl.multiplex.screening.Screening;
import pl.multiplex.shared.SeatId;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class Reservation {

    private final String reservationId;
    private final Screening screening;
    private final Buyer buyer;
    private final Set<SeatId> seatIds;
    private final LocalDateTime createdAt;

    public Reservation(String reservationId, Screening screening, Buyer buyer, Set<SeatId> seatIds, LocalDateTime createdAt) {
        this.reservationId = normalize(reservationId, "reservationId");
        this.screening = Objects.requireNonNull(screening, "screening cannot be null");
        this.buyer = Objects.requireNonNull(buyer, "buyer cannot be null");
        Objects.requireNonNull(seatIds, "seatIds cannot be null");
        if (seatIds.isEmpty()) throw new IllegalArgumentException("seatIds cannot be empty");
        this.seatIds = Collections.unmodifiableSet(seatIds);
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt cannot be null");
    }

    public String getReservationId() {
        return reservationId;
    }

    public Screening getScreening() {
        return screening;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public Set<SeatId> getSeatIds() {
        return seatIds;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    private static String normalize(String s, String field) {
        Objects.requireNonNull(s, field + " cannot be null");
        String out = s.trim();
        if (out.isBlank()) throw new IllegalArgumentException(field + " cannot be blank");
        return out;
    }
}
