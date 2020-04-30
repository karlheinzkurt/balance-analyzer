package org.insaneheadoflettuce.balanceAnalyzer;

import org.insaneheadoflettuce.balanceAnalyzer.model.Cluster;
import org.insaneheadoflettuce.balanceAnalyzer.model.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;

public class TransactionIntervalTest
{
    List<Transaction> transactions;

    @BeforeEach
    void beforeEach()
    {
        transactions = TestCommons.createMockedTransactions();
        when(transactions.get(0).getValueDate()).thenReturn(LocalDate.of(2018, 10, 1));
        when(transactions.get(1).getValueDate()).thenReturn(LocalDate.of(2018, 11, 4));
        when(transactions.get(2).getValueDate()).thenReturn(LocalDate.of(2018, 12, 5));
        when(transactions.get(3).getValueDate()).thenReturn(LocalDate.of(2019, 1, 6));
        when(transactions.get(4).getValueDate()).thenReturn(LocalDate.of(2019, 2, 7));
        when(transactions.get(5).getValueDate()).thenReturn(LocalDate.of(2019, 3, 31));
    }

    @Test
    void instantiation()
    {
        final var range = new TransactionInterval(LocalDate.now(), LocalDate.now(), Cluster.create("bla", true, List.of()));
        Assertions.assertEquals("bla", range.getName());
        Assertions.assertTrue(range.isConsuming());
        Assertions.assertEquals("day", range.getTypeString());
    }

    @Test
    void getIntervalString()
    {
        Assertions.assertEquals("01.09.2018 - 31.10.2019", new TransactionInterval(
                LocalDate.of(2018, 9, 1),
                LocalDate.of(2019, 10, 31),
                Cluster.create("cluster", true, transactions)).getIntervalString());
    }

    @Test
    void getTransactionsInBetween()
    {
        final var intervalTransactions = new TransactionInterval(
                LocalDate.of(2018, 12, 1),
                LocalDate.of(2019, 2, 28),
                Cluster.create("cluster", true, transactions)).getTransactions();
        Assertions.assertEquals(3, intervalTransactions.stream()
                .filter(t -> List.of(12, 1, 2).contains(t.getValueDate().getMonthValue()))
                .count());
    }

    @Test
    void getTransactionsExactMatch()
    {
        final var intervalTransactions = new TransactionInterval(
                LocalDate.of(2018, 10, 1),
                LocalDate.of(2019, 3, 31),
                Cluster.create("cluster", true, transactions)).getTransactions();
        Assertions.assertEquals(6, intervalTransactions.stream()
                .filter(t -> List.of(10, 11, 12, 1, 2, 3).contains(t.getValueDate().getMonthValue()))
                .count());
    }

    @Test
    void getTransactionsOutside()
    {
        final var intervalTransactions = new TransactionInterval(
                LocalDate.of(2016, 1, 1),
                LocalDate.of(2017, 12, 31),
                Cluster.create("cluster", true, transactions)).getTransactions();
        Assertions.assertEquals(0, intervalTransactions.size());
    }

    @Test
    void getTransactionsRightBefore()
    {
        final var intervalTransactions = new TransactionInterval(
                LocalDate.of(2018, 4, 1),
                LocalDate.of(2018, 9, 30),
                Cluster.create("cluster", true, transactions)).getTransactions();
        Assertions.assertEquals(0, intervalTransactions.size());
    }

    @Test
    void getTransactionsPartialBefore()
    {
        final var intervalTransactions = new TransactionInterval(
                LocalDate.of(2018, 4, 1),
                LocalDate.of(2019, 1, 31),
                Cluster.create("cluster", true, transactions)).getTransactions();
        Assertions.assertEquals(4, intervalTransactions.stream()
                .filter(t -> List.of(10, 11, 12, 1).contains(t.getValueDate().getMonthValue()))
                .count());
    }

    @Test
    void getTransactionsRightAfter()
    {
        final var intervalTransactions = new TransactionInterval(
                LocalDate.of(2019, 4, 1),
                LocalDate.of(2019, 10, 31),
                Cluster.create("cluster", true, transactions)).getTransactions();
        Assertions.assertEquals(0, intervalTransactions.size());
    }

    @Test
    void getTransactionsPartialAfter()
    {
        final var intervalTransactions = new TransactionInterval(
                LocalDate.of(2019, 2, 1),
                LocalDate.of(2019, 10, 31),
                Cluster.create("cluster", true, transactions)).getTransactions();
        Assertions.assertEquals(2, intervalTransactions.stream()
                .filter(t -> List.of(2, 3).contains(t.getValueDate().getMonthValue()))
                .count());
    }

    @Test
    void getShortestDayIntervalString()
    {
        Assertions.assertEquals("23.05.2019", TransactionInterval.getShortestPossibleIntervalString(
                LocalDate.of(2019, 5, 23), LocalDate.of(2019, 5, 23)));
        Assertions.assertEquals("23.05.2019 - 24.05.2019", TransactionInterval.getShortestPossibleIntervalString(
                LocalDate.of(2019, 5, 23), LocalDate.of(2019, 5, 24)));
        Assertions.assertEquals("22.05.2019 - 23.05.2019", TransactionInterval.getShortestPossibleIntervalString(
                LocalDate.of(2019, 5, 22), LocalDate.of(2019, 5, 23)));
    }

    @Test
    void getShortestMonthIntervalString()
    {
        Assertions.assertEquals("10.2019", TransactionInterval.getShortestPossibleIntervalString(
                LocalDate.of(2019, 10, 1), LocalDate.of(2019, 10, 31)));
        Assertions.assertEquals("01.10.2019 - 30.10.2019", TransactionInterval.getShortestPossibleIntervalString(
                LocalDate.of(2019, 10, 1), LocalDate.of(2019, 10, 30)));
        Assertions.assertEquals("02.10.2019 - 31.10.2019", TransactionInterval.getShortestPossibleIntervalString(
                LocalDate.of(2019, 10, 2), LocalDate.of(2019, 10, 31)));
    }

    @Test
    void getShortestYearIntervalString()
    {
        Assertions.assertEquals("2019", TransactionInterval.getShortestPossibleIntervalString(
                LocalDate.of(2019, 1, 1), LocalDate.of(2019, 12, 31)));
        Assertions.assertEquals("01.01.2019 - 30.12.2019", TransactionInterval.getShortestPossibleIntervalString(
                LocalDate.of(2019, 1, 1), LocalDate.of(2019, 12, 30)));
        Assertions.assertEquals("02.01.2019 - 31.12.2019", TransactionInterval.getShortestPossibleIntervalString(
                LocalDate.of(2019, 1, 2), LocalDate.of(2019, 12, 31)));
    }
}
