package pl.multiplex.sales;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TicketOrder {

    private final String orderId;
    private final Buyer buyer;
    private final List<Ticket> tickets;
    private final LocalDateTime createdAt;

    public TicketOrder(String orderId, Buyer buyer, List<Ticket> tickets, LocalDateTime createdAt) {
        this.orderId = normalize(orderId, "orderId");
        this.buyer = Objects.requireNonNull(buyer, "buyer cannot be null");
        Objects.requireNonNull(tickets, "tickets cannot be null");
        if (tickets.isEmpty()) throw new IllegalArgumentException("tickets cannot be empty");
        this.tickets = Collections.unmodifiableList(tickets);
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt cannot be null");
    }

    public String getOrderId() {
        return orderId;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public List<Ticket> getTickets() {
        return tickets;
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
