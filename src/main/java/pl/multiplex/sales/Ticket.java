package pl.multiplex.sales;

import pl.multiplex.screening.Screening;
import pl.multiplex.shared.Money;
import pl.multiplex.shared.SeatId;

import java.util.Objects;

public class Ticket {

    private final String ticketId;
    private final Screening screening;
    private final SeatId seatId;
    private final Buyer buyer;
    private final Money price;

    public Ticket(String ticketId, Screening screening, SeatId seatId, Buyer buyer, Money price) {
        this.ticketId = normalize(ticketId, "ticketId");
        this.screening = Objects.requireNonNull(screening, "screening cannot be null");
        this.seatId = Objects.requireNonNull(seatId, "seatId cannot be null");
        this.buyer = Objects.requireNonNull(buyer, "buyer cannot be null");
        this.price = Objects.requireNonNull(price, "price cannot be null");
    }

    public String getTicketId() {
        return ticketId;
    }

    public Screening getScreening() {
        return screening;
    }

    public SeatId getSeatId() {
        return seatId;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public Money getPrice() {
        return price;
    }

    private static String normalize(String s, String field) {
        Objects.requireNonNull(s, field + " cannot be null");
        String out = s.trim();
        if (out.isBlank()) throw new IllegalArgumentException(field + " cannot be blank");
        return out;
    }
}
