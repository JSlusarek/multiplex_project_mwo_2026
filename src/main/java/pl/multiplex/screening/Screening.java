package pl.multiplex.screening;

import pl.multiplex.pricing.TicketFactory;
import pl.multiplex.sales.*;
import pl.multiplex.shared.SeatId;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Screening {

    private static final int CLEANING_BREAK_MIN = 20;

    private final Movie movie;
    private final pl.multiplex.network.Hall hall;
    private final LocalDateTime start;
    private final ScreeningFormat format;
    private final ScreeningClass clazz;

    private final Map<SeatId, SeatStatus> seatStates;

    private final Map<String, Reservation> reservationsById = new LinkedHashMap<>();

    public Screening(Movie movie,
                     pl.multiplex.network.Hall hall,
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

    // -------------------
    // getters
    // -------------------

    public Movie getMovie() {
        return movie;
    }

    public pl.multiplex.network.Hall getHall() {
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


    public LocalDateTime getEnd() {
        return start.plusMinutes(movie.getDurationMin() + CLEANING_BREAK_MIN);
    }

    // -------------------
    // seats
    // -------------------

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

    // -------------------
    // reservation / purchase
    // -------------------

    public Reservation reserveSeats(SeatReservationRequest request) {
        Objects.requireNonNull(request, "request cannot be null");

        for (SeatId id : request.seatIds()) {
            ensureSeatExists(id);
            if (seatStates.get(id) != SeatStatus.FREE) {
                throw new IllegalStateException("Seat is not free: " + id + " (status=" + seatStates.get(id) + ")");
            }
        }

        for (SeatId id : request.seatIds()) {
            seatStates.put(id, SeatStatus.RESERVED);
        }

        String reservationId = UUID.randomUUID().toString();
        Reservation reservation = new Reservation(
                reservationId,
                this,
                request.buyer(),
                Set.copyOf(request.seatIds()),
                LocalDateTime.now()
        );

        reservationsById.put(reservationId, reservation);
        return reservation;
    }

    public void cancelReservation(String reservationId) {
        Objects.requireNonNull(reservationId, "reservationId cannot be null");

        Reservation reservation = reservationsById.remove(reservationId);
        if (reservation == null) {
            throw new NoSuchElementException("Reservation not found: " + reservationId);
        }

        for (SeatId id : reservation.getSeatIds()) {
            ensureSeatExists(id);
            if (seatStates.get(id) == SeatStatus.RESERVED) {
                seatStates.put(id, SeatStatus.FREE);
            }
        }
    }

    /**
     * Zakup biletów: miejsca muszą być FREE albo RESERVED (np. po wcześniejszej rezerwacji).
     * Po zakupie miejsca przechodzą na SOLD i powstaje TicketOrder.
     */
    public TicketOrder buyTickets(TicketPurchaseRequest request, TicketFactory ticketFactory) {
        Objects.requireNonNull(request, "request cannot be null");
        Objects.requireNonNull(ticketFactory, "ticketFactory cannot be null");

        for (SeatId id : request.seatIds()) {
            ensureSeatExists(id);
            SeatStatus status = seatStates.get(id);
            if (status == SeatStatus.SOLD) {
                throw new IllegalStateException("Seat already sold: " + id);
            }
        }

        for (SeatId id : request.seatIds()) {
            seatStates.put(id, SeatStatus.SOLD);
        }

        List<Ticket> tickets = request.seatIds().stream()
                .map(seatId -> ticketFactory.createTicket(request.buyer(), this, seatId))
                .collect(Collectors.toList());

        TicketOrder order = new TicketOrder(
                UUID.randomUUID().toString(),
                request.buyer(),
                tickets,
                LocalDateTime.now()
        );

        if (request.buyer() instanceof Customer customer) {
            customer.addTickets(tickets);
        }


        return order;
    }

    // -------------------
    // helpers
    // -------------------

    private void ensureSeatExists(SeatId id) {
        if (!seatStates.containsKey(id)) {
            throw new NoSuchElementException("Seat not found in this screening: " + id);
        }
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
