package org.insaneheadoflettuce.balance_analyzer;

import org.insaneheadoflettuce.balance_analyzer.model.Transaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestCommons {
    public static Transaction mockTransaction(LocalDate date) {
        return when(mock(Transaction.class).getValueDate())
                .thenReturn(date)
                .getMock();
    }

    static Transaction mockTransaction(String recipient, String purpose) {
        final var mock = mock(Transaction.class);
        when(mock.getRecipientOrPayer()).thenReturn(recipient);
        when(mock.getPurpose()).thenReturn(purpose);
        when(mock.add(any())).thenReturn(mock);
        return mock;
    }

    public static List<Transaction> createMockedTransactions() {
        final List<Transaction> transactions = new ArrayList<>();
        transactions.add(TestCommons.mockTransaction("Cat", "Payed some money"));
        transactions.add(TestCommons.mockTransaction("Karl Heinz Kurt", "Transferred some money"));
        transactions.add(TestCommons.mockTransaction("Jack", "Moved some money"));
        transactions.add(TestCommons.mockTransaction("The fox", "Has stolen some money"));
        transactions.add(TestCommons.mockTransaction("The other Jack", "Regards from Area52"));
        transactions.add(TestCommons.mockTransaction("Edgar", "Ransom"));
        return transactions;
    }
}
