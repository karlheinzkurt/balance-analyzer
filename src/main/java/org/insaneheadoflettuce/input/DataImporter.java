package org.insaneheadoflettuce.input;

import com.google.common.base.Functions;
import org.insaneheadoflettuce.balanceAnalyzer.model.Account;
import org.insaneheadoflettuce.balanceAnalyzer.model.Transaction;
import org.insaneheadoflettuce.input.api.TransactionImporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DataImporter
{
    @Autowired(required = false)
    List<TransactionImporter> transactionImporters;

    Map<String, TransactionImporter> getImporter()
    {
        return transactionImporters.stream().collect(Collectors.toMap(TransactionImporter::getName, Functions.identity()));
    }

    public List<Transaction> importAll(Path dataRoot, List<Account> accounts)
    {
        final var importerMap = getImporter();
        return accounts.stream()
                .flatMap(account -> importerMap.get(account.getInstitution()).doImport(dataRoot, account).stream())
                .collect(Collectors.toList());
    }
}
