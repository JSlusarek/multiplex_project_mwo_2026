package pl.multiplex.pricing;

import pl.multiplex.network.Seat;
import pl.multiplex.screening.Screening;
import pl.multiplex.shared.Money;

public interface PricingPolicy {
    Money priceFor(Screening screening, Seat seat);
}
