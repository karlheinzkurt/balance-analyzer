package org.insaneheadoflettuce.input.csv;

import org.insaneheadoflettuce.balanceAnalyzer.model.Transaction;

public interface CSVTransactionEntry
{
    Transaction toTransaction();
}
