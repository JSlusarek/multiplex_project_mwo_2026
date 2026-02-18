package pl.multiplex.screening;

import pl.multiplex.network.Hall;
import pl.multiplex.shared.SeatId;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Screening {

    private static final int CLEANING_BREAK_MIN = 20;

    private final Movie movie;
    private final Hall hall;
    private final LocalDateTime start;
    private final ScreeningFormat format;
    private final ScreeningClass clazz;

    private final Map<SeatId, SeatStatus> seatStates;

    public Screening(Movie movie,
                     Hall hall,
                     LocalDateTime start,
                     ScreeningFormat format,
                     ScreeningClass clazz) {

        this.movie = Objects.requireNonNull(movie, "movie cannot be null");
        this.hall = Objects.requireNonNull(hall, "hall cannot be null");
        this.start = Objects.requireNonNull(start, "start cannot be null");
        this.format = Objects.requireNonNull(format, "format cannot be null");
        this.clazz = Objects.requireNonNull(clazz, "clazz cannot be null");

        Map<SeatId, SeatStatus> tmp = new LinkedHashMap<>();
        for (SeatId id : hall.getSeatIds()) {
            tmp.put(id, SeatStatus.FREE);
        }
        this.seatStates = tmp;
    }

    public Movie getMovie() {
        return movie;
    }

    public Hall getHall() {
        return hall;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public ScreeningFormat getFormat() {
        return format;
    }

    public ScreeningClass getClazz() {
        return clazz;
    }

    // derived
    public LocalDateTime getEnd() {
        return start.plusMinutes(movie.getDurationMin() + CLEANING_BREAK_MIN);
    }

    public Set<SeatId> getFreeSeats() {
        return seatStates.entrySet().stream()
                .filter(e -> e.getValue() == SeatStatus.FREE)
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableSet());
    }

    public SeatStatus getSeatStatus(SeatId seatId) {
        SeatStatus status = seatStates.get(seatId);
        if (status == null) {
            throw new NoSuchElementException("Seat not found in this screening: " + seatId);
        }
        return status;
    }

    @Override
    public String toString() {
        return "Screening{" +
                "movie=" + movie.getTitle() +
                ", hall=" + hall.getName() +
                ", start=" + start +
                ", format=" + format +
                ", clazz=" + clazz +
                '}';
    }
}
