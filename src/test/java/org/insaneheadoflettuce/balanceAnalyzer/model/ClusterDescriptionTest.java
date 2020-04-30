package org.insaneheadoflettuce.balanceAnalyzer.model;

import com.google.gson.Gson;
import org.insaneheadoflettuce.balanceAnalyzer.TestCommons;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClusterDescriptionTest
{
    List<Transaction> transactions;

    @BeforeEach
    void beforeEach()
    {
        transactions = TestCommons.createMockedTransactions();
    }

    @Test
    void instantiation()
    {
        final var description = ClusterDescription.create("bla");
        assertEquals("bla", description.getName());
    }

    @Test
    void simpleWhitelist()
    {
        final var cluster = new ClusterDescription();
        cluster.setWhiteList(ClusterDescription.Field.PURPOSE, MatchDescription.containsAnyPattern(".*payed.*"));
        cluster.setWhiteList(ClusterDescription.Field.RECIPIENTORPAYER, MatchDescription.containsAnyPattern(".*kurt.*"));

        final var result = cluster.consumeMatching(transactions);

        final var expected = List.of("Cat", "Karl Heinz Kurt");
        assertEquals(2, result.stream().map(Transaction::getRecipientOrPayer).filter(expected::contains).count());
    }

    @Test
    void simpleBlacklist()
    {
        final var cluster = new ClusterDescription();
        cluster.setBlackList(ClusterDescription.Field.RECIPIENTORPAYER, MatchDescription.containsAnyPattern(".*Jack.*"));
        cluster.setWhiteList(ClusterDescription.Field.PURPOSE, MatchDescription.containsAnyPattern(".*some money.*"));

        final var result = cluster.consumeMatching(transactions);

        final var expected = List.of("Cat", "Karl Heinz Kurt", "The fox");
        assertEquals(3, result.stream().map(Transaction::getRecipientOrPayer).filter(expected::contains).count());
    }

    @Test
    void matchesAnyQuotedFromJson()
    {
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
}
