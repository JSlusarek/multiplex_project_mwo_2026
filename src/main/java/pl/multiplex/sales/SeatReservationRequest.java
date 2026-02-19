package pl.multiplex.sales;

import pl.multiplex.shared.SeatId;

import java.util.Objects;
import java.util.Set;

public record SeatReservationRequest(Buyer buyer, Set<SeatId> seatIds) {

    public SeatReservationRequest {
        Objects.requireNonNull(buyer, "buyer cannot be null");
        Objects.requireNonNull(seatIds, "seatIds cannot be null");
        if (seatIds.isEmpty()) throw new IllegalArgumentException("seatIds cannot be empty");
    }
}
