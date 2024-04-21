package org.insaneheadoflettuce.balance_analyzer.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

public class TransactionTest {
    @Test
    void transactionToString() {
        final var transaction = new Transaction();
        transaction.setId(23L);
        transaction.setValueDate(LocalDate.of(2019, 5, 23));
        transaction.setRecipientOrPayer("Frau Holle");
        transaction.setPurpose("Snow");
        transaction.setChecksum("2323");
        Assertions.assertEquals("Id: 23, Value date: 2019-05-23, Recipient or payer: Frau Holle, Purpose: Snow, Checksum: 2323", transaction.toString());
    }

    @Test
    void addClustersToTransactions() {
        final var transaction = new Transaction();
        Assertions.assertFalse(transaction.isClustered());
        Assertions.assertDoesNotThrow(() -> transaction.add(Cluster.create("A", true, List.of())));
        Assertions.assertTrue(transaction.isClustered());
        Assertions.assertThrows(IllegalStateException.class, () -> transaction.add(Cluster.create("A", true, List.of())));
        Assertions.assertTrue(transaction.isClustered());
        Assertions.assertDoesNotThrow(() -> transaction.add(Cluster.create("A", false, List.of())));
        Assertions.assertTrue(transaction.isClustered());
    }
}
