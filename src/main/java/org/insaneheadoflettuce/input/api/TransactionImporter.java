package org.insaneheadoflettuce.input.api;

import org.insaneheadoflettuce.balanceAnalyzer.model.Account;
import org.insaneheadoflettuce.balanceAnalyzer.model.Transaction;

import java.nio.file.Path;
import java.util.List;

public interface TransactionImporter {
    String getName();

    List<Transaction> doImport(Path rootPath, Account account);
}
