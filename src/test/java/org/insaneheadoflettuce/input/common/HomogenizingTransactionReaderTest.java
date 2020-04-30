package org.insaneheadoflettuce.input.common;

import org.insaneheadoflettuce.balanceAnalyzer.model.Transaction;
import org.insaneheadoflettuce.input.api.TransactionFileReader;
import org.insaneheadoflettuce.input.api.TransactionFileReaderFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HomogenizingTransactionReaderTest
{
    TransactionFileReader reader;

    Transaction createTransaction(String checksum, String purpose, String date)
    {
        final var t = new Transaction();
        t.setChecksum(checksum);
        t.setPurpose(purpose);
        t.setValueDate(LocalDate.parse(date));
        return t;
    }

    TransactionFileReader createReader(List<Transaction> transactions)
    {
        final var reader = mock(TransactionFileReader.class);
        when(reader.read()).thenReturn(transactions);
        return reader;
    }

    @BeforeEach
    void beforeEach()
    {
        final var readerFactory = mock(TransactionFileReaderFactory.class);
        when(readerFactory.create(Paths.get("pathA"))).thenAnswer(c -> createReader(List.of(
                createTransaction("123", "TransactionA1", "2020-01-01"),
                createTransaction("345", "TransactionA2", "2020-01-02"),
                createTransaction("951", "TransactionA3", "2020-01-01"))));
        when(readerFactory.create(Paths.get("pathB"))).thenAnswer(c -> createReader(List.of(
                createTransaction("123", "TransactionB1", "2020-01-01"),
                createTransaction("987", "TransactionB2", "2020-01-01"),
                createTransaction("345", "TransactionB3", "2020-01-01"),
                createTransaction("654", "TransactionB4", "2020-01-01"))));
        reader = new HomogenizingTransactionReader(List.of(Paths.get("pathA"), Paths.get("pathB")), readerFactory);
    }

    @Test
    void latestTransactionWithSameChecksumWins()
    {
        final var transactions = reader.read();
        Assertions.assertEquals(5, transactions.size());
        Assertions.assertEquals("TransactionB1", transactions.get(0).getPurpose());
        Assertions.assertEquals("TransactionB3", transactions.get(1).getPurpose());
        Assertions.assertEquals("TransactionB4", transactions.get(2).getPurpose());
        Assertions.assertEquals("TransactionA3", transactions.get(3).getPurpose());
        Assertions.assertEquals("TransactionB2", transactions.get(4).getPurpose());
    }
}
