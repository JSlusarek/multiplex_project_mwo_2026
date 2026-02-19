package pl.multiplex.sales;

import java.util.Objects;

public class Guest implements Buyer {

    private final String alias;

    public Guest(String alias) {
        this.alias = normalize(alias, "alias");
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public String getDisplayName() {
        return alias;
    }

    private static String normalize(String s, String field) {
        Objects.requireNonNull(s, field + " cannot be null");
        String out = s.trim();
        if (out.isBlank()) throw new IllegalArgumentException(field + " cannot be blank");
        return out;
    }
}
