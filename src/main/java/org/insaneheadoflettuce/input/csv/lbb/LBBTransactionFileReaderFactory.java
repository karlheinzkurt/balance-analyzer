package org.insaneheadoflettuce.input.csv.lbb;

import com.opencsv.bean.CsvToBeanBuilder;
import org.insaneheadoflettuce.input.api.TransactionFileReader;
import org.insaneheadoflettuce.input.api.TransactionFileReaderFactory;
import org.insaneheadoflettuce.input.csv.CSVTransactionReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class LBBTransactionFileReaderFactory implements TransactionFileReaderFactory {
    @Override
    public TransactionFileReader create(Path path) {
        try {
            return create(Files.newInputStream(CSVTransactionReader.check(path)));
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not process file: " + path, e);
        }
    }

    public TransactionFileReader create(InputStream csvStream) {
        return new CSVTransactionReader<>(new CsvToBeanBuilder<LBBTransactionEntry>(new InputStreamReader(csvStream, StandardCharsets.ISO_8859_1))
                .withSeparator(';')
                .withType(LBBTransactionEntry.class)
                .build());
    }
}
