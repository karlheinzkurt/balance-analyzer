package org.insaneheadoflettuce.input.api;

import org.insaneheadoflettuce.balance_analyzer.model.Transaction;

import java.util.List;

public interface TransactionFileReader {
    List<Transaction> read();
}
