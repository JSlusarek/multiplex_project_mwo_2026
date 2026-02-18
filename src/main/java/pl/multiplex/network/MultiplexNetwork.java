package pl.multiplex.network;

import java.util.*;

/**
 * Sieć kin (wiele lokalizacji w jednym systemie).
 */
public class MultiplexNetwork {

    private final List<Cinema> cinemas = new ArrayList<>();

    public void addCinema(Cinema cinema) {
        Objects.requireNonNull(cinema, "cinema cannot be null");
        boolean exists = cinemas.stream().anyMatch(c -> c.getName().equalsIgnoreCase(cinema.getName()));
        if (exists) {
            throw new IllegalArgumentException("Cinema with name '" + cinema.getName() + "' already exists");
        }
        cinemas.add(cinema);
    }

    public void removeCinema(Cinema cinema) {
        Objects.requireNonNull(cinema, "cinema cannot be null");
        cinemas.remove(cinema);
    }

    public Cinema findCinema(String name) {
        Objects.requireNonNull(name, "name cannot be null");
        String key = name.trim();
        return cinemas.stream()
                .filter(c -> c.getName().equalsIgnoreCase(key))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Cinema not found: " + key)); // było z dr Turkiem :)
    }

    public List<Cinema> getCinemas() {
        return Collections.unmodifiableList(cinemas);
    }

    @Override
    public String toString() {
        return "MultiplexNetwork{cinemas=" + cinemas.size() + '}';
    }
}
