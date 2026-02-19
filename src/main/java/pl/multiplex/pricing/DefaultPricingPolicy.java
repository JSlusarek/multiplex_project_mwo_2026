package pl.multiplex.pricing;

import pl.multiplex.network.Seat;
import pl.multiplex.screening.Screening;
import pl.multiplex.screening.ScreeningClass;
import pl.multiplex.screening.ScreeningFormat;
import pl.multiplex.shared.Money;
import pl.multiplex.shared.SeatZone;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Prosty cennik domyślny:
 * - baza zależna od strefy (SeatZone)
 * - dopłata za 3D
 * - dopłata za seans VIP
 */
public class DefaultPricingPolicy implements PricingPolicy {

    private final String currency;

    public DefaultPricingPolicy() {
        this("PLN");
    }

    public DefaultPricingPolicy(String currency) {
        this.currency = Objects.requireNonNull(currency, "currency cannot be null").trim().toUpperCase();
        if (this.currency.isBlank()) {
            throw new IllegalArgumentException("currency cannot be blank");
        }
    }

    @Override
    public Money priceFor(Screening screening, Seat seat) {
        Objects.requireNonNull(screening, "screening cannot be null");
        Objects.requireNonNull(seat, "seat cannot be null");

        BigDecimal base = baseForZone(seat.getZone());

        if (screening.getFormat() == ScreeningFormat.THREE_D) {
            base = base.add(BigDecimal.valueOf(6.00));
        }

        if (screening.getClazz() == ScreeningClass.VIP) {
            base = base.add(BigDecimal.valueOf(10.00));
        }

        return new Money(base, currency);
    }

    private BigDecimal baseForZone(SeatZone zone) {
        return switch (zone) {
            case STANDARD -> BigDecimal.valueOf(25.00);
            case VIP -> BigDecimal.valueOf(35.00);
            case PROMO -> BigDecimal.valueOf(18.00);
            case SUPER_PROMO -> BigDecimal.valueOf(12.00);
        };
    }
}
