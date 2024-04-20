package org.insaneheadoflettuce.input.csv;

import com.opencsv.bean.CsvToBean;
import org.insaneheadoflettuce.balanceAnalyzer.model.Transaction;
import org.insaneheadoflettuce.input.api.ChecksumProvider;
import org.insaneheadoflettuce.input.api.TransactionFileReader;
import org.insaneheadoflettuce.input.common.SimpleChecksumProvider;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CSVTransactionReader<T extends CSVTransactionEntry> implements TransactionFileReader {
    private final ChecksumProvider checksumProvider = new SimpleChecksumProvider();
    private final CsvToBean<T> csvToBean;
    private Function<Transaction, Transaction> modificator;

    public static Path check(Path csvPath) {
        final var file = csvPath.toFile();
        if (!file.exists()) {
            throw new IllegalArgumentException("Path not found: " + csvPath.toString());
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException("Path exists but is not of type file: " + csvPath.toString());
        }
        return csvPath;
    }

    public CSVTransactionReader(CsvToBean<T> csvToBean) {
        this(csvToBean, Function.identity());
    }

    public CSVTransactionReader(CsvToBean<T> csvToBean, Function<Transaction, Transaction> modificator) {
        this.csvToBean = csvToBean;
        this.modificator = modificator;
    }

    @Override
    public List<Transaction> read() {
        return csvToBean.parse().stream()
                .map(CSVTransactionEntry::toTransaction)
                .peek(modificator::apply)
                .peek(t -> t.setChecksum(checksumProvider.calculatePurged(
                        //t.getAccount(),
                        Optional.ofNullable(t.getValueDate()).map(Object::toString).orElse(""),
                        t.getPostingText(),
                        t.getPurpose(),
                        t.getRecipientOrPayer(),
                        t.getAmount().toString())))
                .collect(Collectors.toList());
    }
}
