package org.insaneheadoflettuce.input.api;

import org.insaneheadoflettuce.balanceAnalyzer.model.Transaction;

import java.util.List;

public interface TransactionFileReader
{
    List<Transaction> read();
}
