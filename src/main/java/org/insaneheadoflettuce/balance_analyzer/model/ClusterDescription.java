package org.insaneheadoflettuce.balance_analyzer.model;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import jakarta.persistence.*;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;

@Entity
public class ClusterDescription {

    public enum Field {
        PURPOSE,
        RECIPIENTORPAYER,
        POSTINGTEXT,
        VALUEDATE,
        AMOUNT,
        STATE,
    }

    /**
     * Method itself is not serializable, so we have to map it here.
     */
    private static final Map<Field, Function<Transaction, String>> fieldMap = Map.ofEntries(
            Map.entry(Field.PURPOSE, Transaction::getPurpose),
            Map.entry(Field.RECIPIENTORPAYER, Transaction::getRecipientOrPayer),
            Map.entry(Field.POSTINGTEXT, Transaction::getPostingText),
            Map.entry(Field.VALUEDATE, t -> t.getValueDate().toString()),
            Map.entry(Field.AMOUNT, t -> t.getAmount().toString()),
            Map.entry(Field.STATE, t -> t.getState().toString())
    );

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String meta;
    @OneToMany(cascade = CascadeType.PERSIST)
    private final Map<Field, MatchDescription> whiteList = new EnumMap<>(Field.class);
    @OneToMany(cascade = CascadeType.PERSIST)
    private final Map<Field, MatchDescription> blackList = new EnumMap<>(Field.class);

    public static ClusterDescription create(String name) {
        final var cluster = new ClusterDescription();
        cluster.name = name;
        return cluster;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMeta() {
        return meta;
    }

    public ClusterDescription setWhiteList(Field field, MatchDescription matchDescription) {
        this.whiteList.put(field, matchDescription);
        return this;
    }

    public ClusterDescription setBlackList(Field field, MatchDescription matchDescription) {
        this.blackList.put(field, matchDescription);
        return this;
    }

    static boolean hasValidTokens(MatchDescription matchDescription) {
        return Optional.ofNullable(matchDescription)
                .map(MatchDescription::getTokens)
                .stream()
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .anyMatch(token -> !token.isEmpty());
    }

    static Predicate<Transaction> createPredicate(Map.Entry<ClusterDescription.Field, MatchDescription> entry) {
        return transaction -> Optional.ofNullable(entry)
                .map(Map.Entry::getValue)
                .map(MatchDescription::getPattern)
                .flatMap(pattern -> Optional.of(entry)
                        .map(Map.Entry::getKey)
                        .map(fieldMap::get)
                        .map(function -> function.apply(transaction))
                        .map(pattern::matcher))
                .map(Matcher::matches)
                .orElse(false);
    }

    public List<Transaction> consumeMatching(List<Transaction> transactions) {
        final var includeList = whiteList.entrySet().stream()
                .filter(entry -> hasValidTokens(entry.getValue()))
                .map(ClusterDescription::createPredicate)
                .toList();

        final var excludeList = blackList.entrySet().stream()
                .filter(entry -> hasValidTokens(entry.getValue()))
                .map(ClusterDescription::createPredicate)
                .toList();

        return transactions.stream()
                .filter(Predicates.and(Predicates.or(includeList), Predicates.not(Predicates.or(excludeList))))
                .toList();
    }
}
