package pl.multiplex.network;

import pl.multiplex.shared.SeatId;
import pl.multiplex.shared.SeatZone;

import java.util.Objects;

/**
 * Encja miejsca w sali (fizyczna struktura).
 * Uwaga: stan rezerwacji/sprzedaży nie należy do Seat, tylko do Screening.
 */
public class Seat {

    private final SeatId id;
    private final SeatZone zone;

    public Seat(SeatId id, SeatZone zone) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.zone = Objects.requireNonNull(zone, "zone cannot be null");
    }

    public SeatId getId() {
        return id;
    }

    public SeatZone getZone() {
        return zone;
    }

    @Override
    public String toString() {
        return "Seat{" + id + ", zone=" + zone + '}';
    }
}
