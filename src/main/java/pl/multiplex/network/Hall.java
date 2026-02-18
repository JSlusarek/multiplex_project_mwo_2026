package pl.multiplex.network;

import pl.multiplex.shared.SeatId;

import java.util.*;

/**
 * Sala kinowa z zestawem miejsc.
 */
public class Hall {

    private final String name;
    private final Map<SeatId, Seat> seatsById;

    public Hall(String name, Collection<Seat> seats) {
        this.name = normalizeName(name);
        Objects.requireNonNull(seats, "seats cannot be null");

        Map<SeatId, Seat> tmp = new LinkedHashMap<>();
        for (Seat seat : seats) {
            Objects.requireNonNull(seat, "seat cannot be null");
            SeatId id = seat.getId();
            if (tmp.putIfAbsent(id, seat) != null) {
                throw new IllegalArgumentException("Duplicate seat id in hall '" + this.name + "': " + id);
            }
        }
        if (tmp.isEmpty()) {
            throw new IllegalArgumentException("Hall must have at least one seat");
        }
        this.seatsById = tmp;
    }

    public String getName() {
        return name;
    }

    public Seat getSeat(SeatId id) {
        Objects.requireNonNull(id, "id cannot be null");
        Seat seat = seatsById.get(id);
        if (seat == null) {
            throw new NoSuchElementException("Seat not found in hall '" + name + "': " + id);
        }
        return seat;
    }

    /**
     * Zwraca niemutowalny widok miejsc (żeby nikt nie modyfikował kolekcji z zewnątrz).
     */
    public Set<Seat> getSeats() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(seatsById.values()));
    }

    /**
     * Wygodna metoda dla Screening: lista wszystkich SeatId w sali.
     */
    public Set<SeatId> getSeatIds() {
        return Collections.unmodifiableSet(seatsById.keySet());
    }

    private static String normalizeName(String name) {
        Objects.requireNonNull(name, "name cannot be null");
        String n = name.trim();
        if (n.isBlank()) throw new IllegalArgumentException("Hall name cannot be blank");
        return n;
    }

    @Override
    public String toString() {
        return "Hall{" + "name='" + name + '\'' + ", seats=" + seatsById.size() + '}';
    }
}
