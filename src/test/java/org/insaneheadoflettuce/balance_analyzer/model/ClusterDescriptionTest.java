package org.insaneheadoflettuce.balance_analyzer.model;

import com.google.gson.Gson;
import org.insaneheadoflettuce.balance_analyzer.TestCommons;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ClusterDescriptionTest {
    List<Transaction> transactions;

    @BeforeEach
    void beforeEach() {
        transactions = TestCommons.createMockedTransactions();
    }

    @Test
    void instantiation() {
        final var description = ClusterDescription.create("bla");
        assertEquals("bla", description.getName());
    }

    @Test
    void simpleWhitelist() {
        final var cluster = new ClusterDescription();
        cluster.setWhiteList(ClusterDescription.Field.PURPOSE, MatchDescription.containsAnyPattern(".*payed.*"));
        cluster.setWhiteList(ClusterDescription.Field.RECIPIENTORPAYER, MatchDescription.containsAnyPattern(".*kurt.*"));

        final var result = cluster.consumeMatching(transactions);

        final var expected = List.of("Cat", "Karl Heinz Kurt");
        assertEquals(2, result.stream().map(Transaction::getRecipientOrPayer).filter(expected::contains).count());
    }

    @Test
    void simpleBlacklist() {
        final var cluster = new ClusterDescription();
        cluster.setBlackList(ClusterDescription.Field.RECIPIENTORPAYER, MatchDescription.containsAnyPattern(".*Jack.*"));
        cluster.setWhiteList(ClusterDescription.Field.PURPOSE, MatchDescription.containsAnyPattern(".*some money.*"));

        final var result = cluster.consumeMatching(transactions);

        final var expected = List.of("Cat", "Karl Heinz Kurt", "The fox");
        assertEquals(3, result.stream().map(Transaction::getRecipientOrPayer).filter(expected::contains).count());
    }

    @Test
    void matchesAnyQuotedFromJson() {
        final var clusterDescription = new Gson().fromJson("""
                {
                    "name": "Some cluster",
                    "meta": "Fixed costs",
                    "whiteList":
                    {
                        "PURPOSE":
                        {
                            "tokens": ["Area52"],
                            "matchType": "CONTAINS_ANY_QUOTED"
                        }
                    }
                }
                """, ClusterDescription.class);
        assertEquals("Some cluster", clusterDescription.getName());
        assertEquals("Fixed costs", clusterDescription.getMeta());

        final var selected = clusterDescription.consumeMatching(transactions);
        assertEquals(1, selected.size());
        assertEquals("The other Jack", selected.get(0).getRecipientOrPayer());
        assertEquals("Regards from Area52", selected.get(0).getPurpose());
    }

    MatchDescription createMatchDescription(List<String> tokens) {
        return createMatchDescription(null, tokens);
    }

    MatchDescription createMatchDescription(MatchDescription.MatchType type, List<String> tokens) {
        final var description = new MatchDescription();
        description.setTokens(tokens);
        description.setMatchType(type);
        return description;
    }

    @Test
    void hasNoValidTokens() {
        assertFalse(ClusterDescription.hasValidTokens(null));
        assertFalse(ClusterDescription.hasValidTokens(new MatchDescription()));
        assertFalse(ClusterDescription.hasValidTokens(createMatchDescription(List.of())));
        assertFalse(ClusterDescription.hasValidTokens(createMatchDescription(List.of(""))));
    }

    @Test
    void hasValidTokens() {
        assertTrue(ClusterDescription.hasValidTokens(createMatchDescription(List.of("bla"))));
        assertTrue(ClusterDescription.hasValidTokens(createMatchDescription(List.of("", "bla"))));
        assertTrue(ClusterDescription.hasValidTokens(createMatchDescription(Arrays.asList(null, "", "bla"))));
    }

    Transaction createTransaction(String purpose) {
        final var transaction = new Transaction();
        transaction.setPurpose(purpose);
        return transaction;
    }

    @Test
    void createMatchingPredicate() {
        assertTrue(ClusterDescription.createPredicate(Map.entry(
                        ClusterDescription.Field.PURPOSE,
                        createMatchDescription(MatchDescription.MatchType.MATCHES_ANY_QUOTED, List.of("bla"))))
                .apply(createTransaction("bla")));

        assertTrue(ClusterDescription.createPredicate(Map.entry(
                        ClusterDescription.Field.PURPOSE,
                        createMatchDescription(MatchDescription.MatchType.MATCHES_ANY_QUOTED, List.of("foo", "bla"))))
                .apply(createTransaction("bla")));
    }

    @Test
    void createNonMatchingPredicate() {
        assertFalse(ClusterDescription.createPredicate(Map.entry(
                        ClusterDescription.Field.PURPOSE,
                        createMatchDescription(MatchDescription.MatchType.MATCHES_ANY_QUOTED, List.of("bla"))))
                .apply(createTransaction("foo")));
    }

    @Test
    void createMatchingOtherFieldPredicate() {
        assertFalse(ClusterDescription.createPredicate(Map.entry(
                        ClusterDescription.Field.POSTINGTEXT,
                        createMatchDescription(MatchDescription.MatchType.MATCHES_ANY_QUOTED, List.of("bla"))))
                .apply(createTransaction("bla")));
    }
}
