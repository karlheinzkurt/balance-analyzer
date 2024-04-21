package org.insaneheadoflettuce.input.csv.lbb;

import org.insaneheadoflettuce.balance_analyzer.model.Account;
import org.insaneheadoflettuce.balance_analyzer.model.Transaction;
import org.insaneheadoflettuce.input.api.TransactionImporter;
import org.insaneheadoflettuce.input.common.FileCollector;
import org.insaneheadoflettuce.input.common.HomogenizingTransactionReader;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("unused") // Is used by reflection
@Service("LBBTransactionFileImporter")
public class LBBTransactionFileImporter implements TransactionImporter {
    @Override
    public String getName() {
        return "LBB";
    }

    @Override
    public List<Transaction> doImport(Path rootPath, Account account) {
        final var paths = new FileCollector(rootPath)
                .filterByPattern(Pattern.compile("(\\d{8})[-]" + account.getAccountNumber() + "[-]umsatz[.]csv", Pattern.CASE_INSENSITIVE))
                .sortByDate(1, DateTimeFormatter.ofPattern("yyyyMMdd"))
                .collect();
        return new HomogenizingTransactionReader(paths, new LBBTransactionFileReaderFactory())
                .read()
                .stream()
                .peek(t -> t.setAccount(account))
                .collect(Collectors.toList());
    }
}
