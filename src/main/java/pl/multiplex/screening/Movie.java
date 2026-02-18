package pl.multiplex.screening;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Movie {

    private final String title;
    private final String director;
    private final int durationMin;
    private final LanguageOption language;
    private final List<String> themes;
    private final AgeRating ageRating;

    public Movie(String title,
                 String director,
                 int durationMin,
                 LanguageOption language,
                 List<String> themes,
                 AgeRating ageRating) {

        this.title = normalize(title, "title");
        this.director = normalize(director, "director");

        if (durationMin <= 0) {
            throw new IllegalArgumentException("durationMin must be > 0");
        }
        this.durationMin = durationMin;

        this.language = Objects.requireNonNull(language, "language cannot be null");
        this.ageRating = Objects.requireNonNull(ageRating, "ageRating cannot be null");

        Objects.requireNonNull(themes, "themes cannot be null");
        this.themes = Collections.unmodifiableList(new ArrayList<>(themes));
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public int getDurationMin() {
        return durationMin;
    }

    public LanguageOption getLanguage() {
        return language;
    }

    public List<String> getThemes() {
        return themes;
    }

    public AgeRating getAgeRating() {
        return ageRating;
    }

    private static String normalize(String s, String field) {
        Objects.requireNonNull(s, field + " cannot be null");
        String out = s.trim();
        if (out.isBlank()) throw new IllegalArgumentException(field + " cannot be blank");
        return out;
    }

    @Override
    public String toString() {
        return "Movie{" + "title='" + title + '\'' + ", director='" + director + '\'' + ", durationMin=" + durationMin + '}';
    }
}
