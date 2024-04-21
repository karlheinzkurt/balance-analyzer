package org.insaneheadoflettuce.input.csv;

import org.insaneheadoflettuce.balance_analyzer.model.Transaction;

public interface CSVTransactionEntry {
    Transaction toTransaction();
}
