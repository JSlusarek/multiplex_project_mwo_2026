package pl.multiplex.sales;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Customer implements Buyer {

    private final String customerId;
    private final String firstName;
    private final String lastName;

    private final List<Ticket> tickets = new ArrayList<>();

    public Customer(String customerId, String firstName, String lastName) {
        this.customerId = normalize(customerId, "customerId");
        this.firstName = normalize(firstName, "firstName");
        this.lastName = normalize(lastName, "lastName");
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<Ticket> getTickets() {
        return Collections.unmodifiableList(tickets);
    }

    public void addTickets(List<Ticket> newTickets) {
        Objects.requireNonNull(newTickets, "newTickets cannot be null");
        tickets.addAll(newTickets);
    }

    @Override
    public String getDisplayName() {
        return firstName + " " + lastName;
    }

    private static String normalize(String s, String field) {
        Objects.requireNonNull(s, field + " cannot be null");
        String out = s.trim();
        if (out.isBlank()) throw new IllegalArgumentException(field + " cannot be blank");
        return out;
    }
}
