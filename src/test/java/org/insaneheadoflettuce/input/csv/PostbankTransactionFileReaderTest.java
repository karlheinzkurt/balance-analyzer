package org.insaneheadoflettuce.input.csv;

import org.insaneheadoflettuce.balanceAnalyzer.Number;
import org.insaneheadoflettuce.balanceAnalyzer.model.Transaction;
import org.insaneheadoflettuce.input.api.TransactionFileReaderFactory;
import org.insaneheadoflettuce.input.csv.postbank.PostbankTransactionFileReaderFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Objects;

public class PostbankTransactionFileReaderTest
{
    private static TransactionFileReaderFactory readerFactory;

    @BeforeAll
    static void beforeAll()
    {
        readerFactory = new PostbankTransactionFileReaderFactory();
    }

    @Test
    void readFromFile()
    {
        final var reader = Assertions.assertDoesNotThrow(() -> readerFactory.create(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("Postbank/Umsatzauskunft_KtoNr0123456789_20-01-2020_20-20-20.csv")).getPath())));
        final var transactions = Assertions.assertDoesNotThrow(reader::read);
        final var value = transactions.stream()
                .map(Transaction::getAmount)
                .mapToDouble(Number::getValue)
                .reduce(23.5, Double::sum);
        Assertions.assertEquals(3, transactions.size());
        Assertions.assertEquals(23.5 - 0.75 - 1.00 - 55.00, value);
        {
            final var transaction = transactions.get(0);
            Assertions.assertEquals("Verwendungszweck 4", transaction.getPurpose());
            Assertions.assertEquals("Auftraggeber 4", transaction.getRecipientOrPayer());
            Assertions.assertEquals("Lastbuchung", transaction.getPostingText());
            Assertions.assertEquals(-0.75, transaction.getAmount().getValue());
            Assertions.assertEquals("2020-01-20", transaction.getValueDate().toString()); // Taken the "Buchungsdatum" because "Wertstellungsdatum" is not set for pending transactions
            Assertions.assertEquals(Transaction.State.PENDING, transaction.getState());
        }
        {
            final var transaction = transactions.get(1);
            Assertions.assertEquals("Verwendungszweck 3", transaction.getPurpose());
            Assertions.assertEquals("Auftraggeber 3/Empfänger 3", transaction.getRecipientOrPayer());
            Assertions.assertEquals("Dauerauftrag", transaction.getPostingText());
            Assertions.assertEquals(-1.00, transaction.getAmount().getValue());
            Assertions.assertEquals("2020-01-15", transaction.getValueDate().toString());
            Assertions.assertEquals(Transaction.State.BOOKED, transaction.getState());
        }
        {
            final var transaction = transactions.get(2);
            Assertions.assertEquals("Verwendungszweck 4", transaction.getPurpose());
            Assertions.assertEquals("Auftraggeber 4/Empfänger 4", transaction.getRecipientOrPayer());
            Assertions.assertEquals("Überweisung", transaction.getPostingText());
            Assertions.assertEquals(-55.00, transaction.getAmount().getValue());
            Assertions.assertEquals("2020-01-15", transaction.getValueDate().toString());
            Assertions.assertEquals(Transaction.State.BOOKED, transaction.getState());
        }
    }
}
