package org.insaneheadoflettuce.input.csv.postbank;

import org.insaneheadoflettuce.balanceAnalyzer.model.Account;
import org.insaneheadoflettuce.balanceAnalyzer.model.Transaction;
import org.insaneheadoflettuce.input.api.TransactionImporter;
import org.insaneheadoflettuce.input.common.FileCollector;
import org.insaneheadoflettuce.input.common.HomogenizingTransactionReader;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service("PostbankTransactionFileImporter")
public class PostbankTransactionFileImporter implements TransactionImporter
{
    @Override
    public String getName()
    {
        return "Postbank";
    }

    @Override
    public List<Transaction> doImport(Path rootPath, Account account)
    {
        final var paths = new FileCollector(rootPath)
                .filterByPattern(Pattern.compile("Umsatzauskunft_KtoNr" + account.getAccountNumber() + "_(\\d{2}[-]\\d{2}[-]\\d{4}).*?[.]csv", Pattern.CASE_INSENSITIVE))
                .sortByDate(1, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                .collect();
        return new HomogenizingTransactionReader(paths, new PostbankTransactionFileReaderFactory())
                .read()
                .stream()
                .peek(t -> t.setAccount(account))
                .collect(Collectors.toList());
    }
}
