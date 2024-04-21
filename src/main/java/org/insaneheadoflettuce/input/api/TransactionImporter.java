package org.insaneheadoflettuce.input.api;

import org.insaneheadoflettuce.balance_analyzer.model.Account;
import org.insaneheadoflettuce.balance_analyzer.model.Transaction;

import java.nio.file.Path;
import java.util.List;

public interface TransactionImporter {
    String getName();

    List<Transaction> doImport(Path rootPath, Account account);
}
