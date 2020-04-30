package org.insaneheadoflettuce.balanceAnalyzer.model;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Entity
public class ClusterDescription
{
    public enum Field
    {
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
    private static Map<Field, Function<Transaction, String>> fieldMap = Map.ofEntries(
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
    private Map<Field, MatchDescription> whiteList = new HashMap<>();
    @OneToMany(cascade = CascadeType.PERSIST)
    private Map<Field, MatchDescription> blackList = new HashMap<>();

    public static ClusterDescription create(String name)
    {
        final var cluster = new ClusterDescription();
        cluster.name = name;
        return cluster;
    }

    public Long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getMeta()
    {
        return meta;
    }

    public ClusterDescription setWhiteList(Field field, MatchDescription matchDescription)
    {
        this.whiteList.put(field, matchDescription);
        return this;
    }

    public ClusterDescription setBlackList(Field field, MatchDescription matchDescription)
    {
        this.blackList.put(field, matchDescription);
        return this;
    }

    // @Formatter: off
    public List<Transaction> consumeMatching(List<Transaction> transactions)
    {
        final List<Predicate<Transaction>> white = whiteList.entrySet().stream().map(entry ->
                (Predicate<Transaction>) transaction -> entry.getValue().getPattern().matcher(fieldMap.get(entry.getKey()).apply(transaction)).matches())
                .collect(Collectors.toList());
        final List<Predicate<Transaction>> black = blackList.entrySet().stream().map(entry ->
                (Predicate<Transaction>) transaction -> entry.getValue().getPattern().matcher(fieldMap.get(entry.getKey()).apply(transaction)).matches())
                .collect(Collectors.toList());
        return transactions.stream()
                .filter(Predicates.and(Predicates.or(white), Predicates.not(Predicates.or(black))))
                .collect(Collectors.toList());
    }
    // @Formatter:on
}
