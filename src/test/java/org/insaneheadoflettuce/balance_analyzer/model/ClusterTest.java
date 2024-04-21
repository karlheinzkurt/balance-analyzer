package org.insaneheadoflettuce.balance_analyzer.model;

import org.insaneheadoflettuce.balance_analyzer.Number;
import org.insaneheadoflettuce.balance_analyzer.TestCommons;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class ClusterTest {
    List<Transaction> transactions;

    @BeforeEach
    void beforeEach() {
        transactions = TestCommons.createMockedTransactions();
        when(transactions.get(0).getAmount()).thenReturn(new Number(2.));
        when(transactions.get(1).getAmount()).thenReturn(new Number(-3.));
        when(transactions.get(2).getAmount()).thenReturn(new Number(5.));
        when(transactions.get(3).getAmount()).thenReturn(new Number(-7.));
        when(transactions.get(4).getAmount()).thenReturn(new Number(11.));
        when(transactions.get(5).getAmount()).thenReturn(new Number(0.));
    }

    @Test
    void getSize() {
        Assertions.assertEquals(6, Cluster.create("size", true, transactions).getSize());
    }

    @Test
    void getDifferentialMovement() {
        final var cluster = Cluster.create("diff", true, transactions);
        Assertions.assertEquals(2. - 3. + 5. - 7. + 11., cluster.getDifferentialMovement().getValue());
    }

    @Test
    void getAbsoluteMovement() {
        final var cluster = Cluster.create("abs", true, transactions);
        Assertions.assertEquals(2. + 3. + 5. + 7. + 11., cluster.getAbsoluteMovement().getValue());
    }

    @Test
    void getPositiveMovement() {
        final var cluster = Cluster.create("pos", true, transactions);
        Assertions.assertEquals(2. + 5. + 11., cluster.getPositiveMovement().getValue());
    }

    @Test
    void getNegativeMovement() {
        final var cluster = Cluster.create("neg", true, transactions);
        Assertions.assertEquals(-3. - 7., cluster.getNegativeMovement().getValue());
    }

    @Test
    void clusterConsumesTransactions() {
        final List<Transaction> t = List.of(new Transaction(), new Transaction());
        Assertions.assertFalse(t.stream().anyMatch(Transaction::isClustered));
        final var cluster = Cluster.create("Consuming", true, t);
        Assertions.assertEquals(2, t.stream().filter(Transaction::isClustered).count());
    }

    @Test
    void clusterDoesNotConsumeTransactions() {
        final List<Transaction> t = List.of(new Transaction(), new Transaction());
        Assertions.assertFalse(t.stream().anyMatch(Transaction::isClustered));
        final var cluster = Cluster.create("Unconsuming", false, t);
        Assertions.assertFalse(t.stream().anyMatch(Transaction::isClustered));
    }

    @Test
    void doubleConsumeFails() {
        final var transaction = new Transaction();
        Assertions.assertFalse(transaction.isClustered());
        Assertions.assertDoesNotThrow(() -> Cluster.create("Consuming", true, List.of(transaction)));
        Assertions.assertTrue(transaction.isClustered());
        final var thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> Cluster.create("Consuming", true, List.of(transaction)));
        Assertions.assertEquals("Transaction cannot be consumed twice", thrown.getMessage());
    }

    @Test
    void ascendingDifferentialMovementComparator() {
        when(transactions.get(5).getAmount()).thenReturn(new Number(1.));

        final var smallerCluster = spy(Cluster.class);
        when(smallerCluster.getTransactions()).thenReturn(transactions.subList(0, 3));
        final var largerCluster = spy(Cluster.class);
        when(largerCluster.getTransactions()).thenReturn(transactions.subList(3, 6));
        final var sorted = Stream.of(largerCluster, smallerCluster).sorted(Cluster.ascendingComparator).collect(Collectors.toList());
        Assertions.assertEquals(4., sorted.get(0).getDifferentialMovement().getValue());
        Assertions.assertEquals(5., sorted.get(1).getDifferentialMovement().getValue());
    }

    @Test
    void descendingDifferentialMovementComparator() {
        when(transactions.get(5).getAmount()).thenReturn(new Number(1.));

        final var smallerCluster = spy(Cluster.class);
        when(smallerCluster.getTransactions()).thenReturn(transactions.subList(0, 3));
        final var largerCluster = spy(Cluster.class);
        when(largerCluster.getTransactions()).thenReturn(transactions.subList(3, 6));
        final var sorted = Stream.of(largerCluster, smallerCluster).sorted(Cluster.descendingComparator).collect(Collectors.toList());
        Assertions.assertEquals(5., sorted.get(0).getDifferentialMovement().getValue());
        Assertions.assertEquals(4., sorted.get(1).getDifferentialMovement().getValue());
    }
}
