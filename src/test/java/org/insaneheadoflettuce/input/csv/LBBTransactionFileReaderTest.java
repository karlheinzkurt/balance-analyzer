package org.insaneheadoflettuce.input.csv;

import org.insaneheadoflettuce.balanceAnalyzer.Number;
import org.insaneheadoflettuce.balanceAnalyzer.model.Transaction;
import org.insaneheadoflettuce.input.api.TransactionFileReaderFactory;
import org.insaneheadoflettuce.input.csv.lbb.LBBTransactionFileReaderFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Objects;

public class LBBTransactionFileReaderTest
{
    private static TransactionFileReaderFactory readerFactory;

    @BeforeAll
    static void beforeAll()
    {
        readerFactory = new LBBTransactionFileReaderFactory();
    }

    @Test
    void readFromFile()
    {
        final var reader = readerFactory.create(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("LBB/20191201-0123456789-umsatz.csv")).getPath()));
        final var transactions = Assertions.assertDoesNotThrow(reader::read);
        final var value = transactions.stream()
                .map(Transaction::getAmount)
                .mapToDouble(Number::getValue)
                .reduce(23.5, Double::sum);
        Assertions.assertEquals(3, transactions.size());
        Assertions.assertEquals(23.5 - 55.59 + 83.89 - 34.53, value);
        {
            final var transaction = transactions.get(0);
            Assertions.assertEquals("Verwendungszweck 1", transaction.getPurpose());
            Assertions.assertEquals("Beguenstigter 1/Zahlungspflichtiger 1", transaction.getRecipientOrPayer());
            Assertions.assertEquals("FOLGELASTSCHRIFT", transaction.getPostingText());
            Assertions.assertEquals(-55.59, transaction.getAmount().getValue());
            Assertions.assertEquals("2019-12-02", transaction.getValueDate().toString());
            Assertions.assertEquals(Transaction.State.PENDING, transaction.getState());
        }
        {
            final var transaction = transactions.get(1);
            Assertions.assertEquals("Verwendungszweck 2", transaction.getPurpose());
            Assertions.assertEquals("Beguenstigter 2/Zahlungspflichtiger 2", transaction.getRecipientOrPayer());
            Assertions.assertEquals("LOHN GEHALT", transaction.getPostingText());
            Assertions.assertEquals(83.89, transaction.getAmount().getValue());
            Assertions.assertEquals("2019-11-28", transaction.getValueDate().toString());
            Assertions.assertEquals(Transaction.State.BOOKED, transaction.getState());
        }
        {
            final var transaction = transactions.get(2);
            Assertions.assertEquals("Verwendungszweck 3", transaction.getPurpose());
            Assertions.assertEquals("Beguenstigter 3/Zahlungspflichtiger 3", transaction.getRecipientOrPayer());
            Assertions.assertEquals("KARTENZAHLUNG", transaction.getPostingText());
            Assertions.assertEquals(-34.53, transaction.getAmount().getValue());
            Assertions.assertEquals("2019-11-28", transaction.getValueDate().toString());
            Assertions.assertEquals(Transaction.State.BOOKED, transaction.getState());
        }
    }

    @Test
    void readEmptyFile()
    {
        final var reader = readerFactory.create(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("LBB/20191201-empty-umsatz.csv")).getPath()));
        final var transactions = Assertions.assertDoesNotThrow(reader::read);
        Assertions.assertEquals(0, transactions.size());
    }

    @Test
    void readNotExistingFile()
    {
        final var thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> readerFactory.create(Paths.get("j2h3g42jh34g2j34g23g4j234gj2h34g.csv")));
        Assertions.assertTrue(thrown.getMessage().startsWith("Path not found: "));
        Assertions.assertTrue(thrown.getMessage().contains("j2h3g42jh34g2j34g23g4j234gj2h34g"));
    }

    @Test
    void readDirectoryTypePath()
    {
        final var thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> readerFactory.create(Paths.get(".").toAbsolutePath()));
        Assertions.assertTrue(thrown.getMessage().startsWith("Path exists but is not of type file: "));
    }
}
