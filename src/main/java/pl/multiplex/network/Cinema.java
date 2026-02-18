package pl.multiplex.network;

import pl.multiplex.screening.Movie;
import pl.multiplex.screening.Screening;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Kino (lokalizacja) – przechowuje sale oraz zaplanowane seanse (repertuar).
 * Uwaga: Cinema nie zarządza rezerwacjami/sprzedażą miejsc – to jest w Screening !!!
 */
public class Cinema {

    private final String name;
    private final String address;

    private final List<Hall> halls = new ArrayList<>();
    private final List<Screening> screenings = new ArrayList<>();

    public Cinema(String name, String address) {
        this.name = normalize(name, "name");
        this.address = normalize(address, "address");
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    // -------------------
    // Halls
    // -------------------

    public void addHall(Hall hall) {
        Objects.requireNonNull(hall, "hall cannot be null");
        boolean exists = halls.stream().anyMatch(h -> h.getName().equalsIgnoreCase(hall.getName()));
        if (exists) {
            throw new IllegalArgumentException("Hall with name '" + hall.getName() + "' already exists in cinema '" + name + "'");
        }
        halls.add(hall);
    }

    public List<Hall> getHalls() {
        return Collections.unmodifiableList(halls);
    }

    public Hall findHall(String hallName) {
        String key = normalize(hallName, "hallName");
        return halls.stream()
                .filter(h -> h.getName().equalsIgnoreCase(key))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Hall not found in cinema '" + name + "': " + key));
    }

    // -------------------
    // Screenings / Programme
    // -------------------

    public void schedule(Screening screening) {
        Objects.requireNonNull(screening, "screening cannot be null");

        boolean hallBelongs = halls.stream().anyMatch(h -> h.getName().equalsIgnoreCase(screening.getHall().getName()));
        if (!hallBelongs) {
            throw new IllegalArgumentException("Cannot schedule screening in hall not registered in cinema '" + name + "'");
        }

        for (Screening existing : screenings) {
            boolean sameHall = existing.getHall().getName().equalsIgnoreCase(screening.getHall().getName());
            if (!sameHall) continue;

            if (overlaps(existing.getStart(), existing.getEnd(), screening.getStart(), screening.getEnd())) {
                throw new IllegalArgumentException(
                        "Screening time conflict in hall '" + screening.getHall().getName() + "' for cinema '" + name + "': "
                                + existing.getStart() + " - " + existing.getEnd()
                                + " overlaps with "
                                + screening.getStart() + " - " + screening.getEnd()
                );
            }
        }

        screenings.add(screening);
    }

    public List<Screening> getScreenings() {
        return Collections.unmodifiableList(screenings);
    }

    public List<Screening> getProgrammeNextWeek(LocalDate today) {
        Objects.requireNonNull(today, "today cannot be null");
        LocalDate from = today;
        LocalDate to = today.plusDays(7);
        return getProgramme(from, to);
    }



    public void printProgramme(LocalDate from, LocalDate to) {
        List<Screening> programme = getProgramme(from, to);
        System.out.println("Programme for cinema: " + name + " (" + address + ")");
        if (programme.isEmpty()) {
            System.out.println("  [no screenings]");
            return;
        }
        for (Screening s : programme) {
            System.out.println("  " + s.getStart() + " | " + s.getHall().getName() + " | "
                    + s.getMovie().getTitle() + " | " + s.getFormat() + " | " + s.getClazz());
        }
    }

    public List<Movie> findMovie(String query) {
        String q = normalize(query, "query").toLowerCase();

        Map<String, Movie> unique = new LinkedHashMap<>();
        for (Screening s : screenings) {
            Movie m = s.getMovie();
            String key = (m.getTitle() + "|" + m.getDirector()).toLowerCase();
            if (m.getTitle().toLowerCase().contains(q) || m.getDirector().toLowerCase().contains(q)) {
                unique.putIfAbsent(key, m);
            }
        }
        return List.copyOf(unique.values());
    }

    // -------------------
    // helpers
    // -------------------
    private List<Screening> getProgramme(LocalDate from, LocalDate to) {
        Objects.requireNonNull(from, "from cannot be null");
        Objects.requireNonNull(to, "to cannot be null");

        LocalDateTime startInclusive = from.atStartOfDay();
        LocalDateTime endExclusive = to.plusDays(1).atStartOfDay();

        return screenings.stream()
                .filter(s -> !s.getStart().isBefore(startInclusive) && s.getStart().isBefore(endExclusive))
                .sorted(Comparator.comparing(Screening::getStart)
                        .thenComparing(s -> s.getHall().getName(), String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private static boolean overlaps(LocalDateTime aStart, LocalDateTime aEnd,
                                    LocalDateTime bStart, LocalDateTime bEnd) {
        return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
    }

    private static String normalize(String s, String field) {
        Objects.requireNonNull(s, field + " cannot be null");
        String out = s.trim();
        if (out.isBlank()) throw new IllegalArgumentException(field + " cannot be blank");
        return out;
    }

    @Override
    public String toString() {
        return "Cinema{" + "name='" + name + '\'' + ", address='" + address + '\''
                + ", halls=" + halls.size() + ", screenings=" + screenings.size() + '}';
    }
}
