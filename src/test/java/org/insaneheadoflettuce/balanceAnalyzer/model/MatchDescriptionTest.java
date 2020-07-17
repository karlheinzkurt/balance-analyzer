package org.insaneheadoflettuce.balanceAnalyzer.model;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MatchDescriptionTest
{
    @Test
    void containsAnyQuoted()
    {
        final var items = List.of("Brown cat", "Old DOG", "Simple mirror", "Gray cat", "Young dog");
        final var description = MatchDescription.containsAnyQuoted("cat", "dog");
        final var expected = List.of("Brown cat", "Old DOG", "Gray cat", "Young dog");
        assertEquals(4, items.stream().filter(i -> description.getPattern().matcher(i).matches()).filter(expected::contains).count());
    }

    @Test
    void matchesAnyQuoted()
    {
        final var items = List.of("Brown cat", "Old DOG", "Simple mirror", "Gray cat", "Young dog");
        final var description = MatchDescription.matchesAnyQuoted("gray cat", "old dog", "dog", "cat");
        final var expected = List.of("Old DOG", "Gray cat");
        assertEquals(2, items.stream().filter(i -> description.getPattern().matcher(i).matches()).filter(expected::contains).count());
    }

    @Test
    void quoteTokensTest()
    {
        final var matchDescription = new Gson().fromJson("""
                {
                    "tokens": [".*"],
                    "matchType": "MATCHES_ANY_QUOTED"
                }
                """, MatchDescription.class);
        assertTrue(matchDescription.getPattern().matcher(".*").matches());
        assertFalse(matchDescription.getPattern().matcher("..*").matches());
    }

    @Test
    void invalidMatchTypeTest()
    {
        final var matchDescription = new Gson().fromJson("""
                {
                    "tokens": ["bla"],
                    "matchType": "blubber"
                }
                """, MatchDescription.class);
        assertThrows(IllegalStateException.class, () -> matchDescription.getPattern().matcher(".*").matches());
    }

    @Test
    void containsAnyQuotedFromJson()
    {
        final var matchDescription = new Gson().fromJson("""
                {
                    "tokens": ["Recipient A", "Payer A", "Recipient B"],
                    "matchType": "CONTAINS_ANY_QUOTED"
                }
                """, MatchDescription.class);
        assertEquals(MatchDescription.MatchType.CONTAINS_ANY_QUOTED, matchDescription.getMatchType());
        assertEquals(List.of("Recipient A", "Payer A", "Recipient B"), matchDescription.getTokens());
        assertEquals(".*?(\\QRecipient A\\E|\\QPayer A\\E|\\QRecipient B\\E).*?", matchDescription.getPatternString());
    }

    @Test
    void matchesAnyQuotedFromJson()
    {
        final var matchDescription = new Gson().fromJson("""
                {
                    "tokens": ["Recipient A", "Payer A", "Recipient B"],
                    "matchType": "MATCHES_ANY_QUOTED"
                }
                """, MatchDescription.class);
        assertEquals(MatchDescription.MatchType.MATCHES_ANY_QUOTED, matchDescription.getMatchType());
        assertEquals(List.of("Recipient A", "Payer A", "Recipient B"), matchDescription.getTokens());
        assertEquals("^\\QRecipient A\\E|\\QPayer A\\E|\\QRecipient B\\E$", matchDescription.getPatternString());
    }
}
