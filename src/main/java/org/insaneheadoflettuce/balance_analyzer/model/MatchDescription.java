package org.insaneheadoflettuce.balance_analyzer.model;

import jakarta.persistence.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Entity
public class MatchDescription {
    private static MatchDescription create(MatchType matchType, List<String> tokens) {
        final var matchDescription = new MatchDescription();
        matchDescription.matchType = matchType;
        matchDescription.tokens = tokens;
        return matchDescription;
    }

    public static MatchDescription containsAnyQuoted(List<String> items) {
        return create(MatchType.CONTAINS_ANY_QUOTED, items);
    }

    public static MatchDescription containsAnyQuoted(String... items) {
        return containsAnyQuoted(Arrays.asList(items));
    }

    public static MatchDescription containsAnyPattern(List<String> items) {
        return create(MatchType.CONTAINS_ANY_PATTERN, items);
    }

    public static MatchDescription containsAnyPattern(String... items) {
        return containsAnyPattern(Arrays.asList(items));
    }

    public static MatchDescription matchesAnyQuoted(List<String> items) {
        return create(MatchType.MATCHES_ANY_QUOTED, items);
    }

    public static MatchDescription matchesAnyQuoted(String... items) {
        return matchesAnyQuoted(Arrays.asList(items));
    }

    public static MatchDescription matchesAnyPattern(List<String> items) {
        return create(MatchType.MATCHES_ANY_PATTERN, items);
    }

    public static MatchDescription matchesAnyPattern(String... items) {
        return matchesAnyPattern(Arrays.asList(items));
    }

    public enum MatchType {
        CONTAINS_ANY_QUOTED,
        CONTAINS_ANY_PATTERN,
        MATCHES_ANY_QUOTED,
        MATCHES_ANY_PATTERN
    }

    private static final Map<MatchType, Function<List<String>, String>> matchTypeMap = Map.ofEntries(
            Map.entry(MatchType.CONTAINS_ANY_QUOTED, t -> ".*?(" + t.stream()
                    .filter(Predicate.not(String::isEmpty))
                    .map(Pattern::quote)
                    .collect(Collectors.joining("|")) + ").*?"),
            Map.entry(MatchType.CONTAINS_ANY_PATTERN, t -> ".*?(" + t.stream()
                    .filter(Predicate.not(String::isEmpty))
                    .collect(Collectors.joining("|")) + ").*?"),
            Map.entry(MatchType.MATCHES_ANY_QUOTED, t -> "^" + t.stream()
                    .filter(Predicate.not(String::isEmpty))
                    .map(Pattern::quote)
                    .collect(Collectors.joining("|")) + "$"),
            Map.entry(MatchType.MATCHES_ANY_PATTERN, t -> "^" + t.stream()
                    .filter(Predicate.not(String::isEmpty))
                    .collect(Collectors.joining("|")) + "$")
    );

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private MatchType matchType;
    @ElementCollection
    private List<String> tokens;

    public MatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(MatchType matchType) {
        this.matchType = matchType;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    public Pattern getPattern() {
        return Pattern.compile(getPatternString(), Pattern.CASE_INSENSITIVE);
    }

    String getPatternString() {
        if (matchType == null) {
            throw new IllegalStateException("Invalid match type");
        }
        if (tokens == null) {
            throw new IllegalStateException("Invalid tokens");
        }
        return matchTypeMap.get(matchType).apply(tokens);
    }
}
