package pl.multiplex.pricing;

import pl.multiplex.network.Seat;
import pl.multiplex.sales.Buyer;
import pl.multiplex.sales.Ticket;
import pl.multiplex.screening.Screening;
import pl.multiplex.shared.Money;
import pl.multiplex.shared.SeatId;

import java.util.Objects;
import java.util.UUID;

/**
 * Fabryka biletów: jedno miejsce, które wie jak tworzyć Ticket
 * i jak policzyć cenę (przez PricingPolicy).
 */
public class TicketFactory {

    private final PricingPolicy pricing;

    public TicketFactory(PricingPolicy pricing) {
        this.pricing = Objects.requireNonNull(pricing, "pricing cannot be null");
    }

    public Ticket createTicket(Buyer buyer, Screening screening, SeatId seatId) {
        Objects.requireNonNull(buyer, "buyer cannot be null");
        Objects.requireNonNull(screening, "screening cannot be null");
        Objects.requireNonNull(seatId, "seatId cannot be null");

        // Seat potrzebny do ustalenia ceny (zone)
        Seat seat = screening.getHall().getSeat(seatId);

        Money price = pricing.priceFor(screening, seat);

        String ticketId = UUID.randomUUID().toString();
        return new Ticket(ticketId, screening, seatId, buyer, price);
    }
}
