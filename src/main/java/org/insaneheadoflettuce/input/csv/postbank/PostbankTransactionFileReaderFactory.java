package org.insaneheadoflettuce.input.csv.postbank;

import com.opencsv.bean.CsvToBeanBuilder;
import org.insaneheadoflettuce.balanceAnalyzer.model.Transaction;
import org.insaneheadoflettuce.balanceAnalyzer.utility.IBANValidator;
import org.insaneheadoflettuce.input.api.TransactionFileReader;
import org.insaneheadoflettuce.input.api.TransactionFileReaderFactory;
import org.insaneheadoflettuce.input.csv.CSVTransactionReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PostbankTransactionFileReaderFactory implements TransactionFileReaderFactory {
    static final char separator = ';';

    @Override
    public TransactionFileReader create(Path path) {
        try {
            return create(Files.readAllLines(CSVTransactionReader.check(path), Charset.forName("Windows-1252")));
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not process file: " + path, e);
        }
    }

    private enum Field {
        NAME, IBAN, BOOKED, PENDING
    }

    private static class Item {
        final String token;
        String line;
        int index;

        Item(String token) {
            this.token = token;
        }

        void set(int index, String line) {
            this.index = index;
            this.line = line;
        }

        boolean matches(String line) {
            return line.startsWith(token);
        }
    }

    public TransactionFileReader create(List<String> lines) {
        // TODO This is a hack!!
        final var matches = Map.of(
                Field.NAME, new Item("Name" + separator),
                Field.IBAN, new Item("IBAN" + separator),
                Field.PENDING, new Item("Umsätze in den nächsten 14 Tagen" + separator),
                Field.BOOKED, new Item("gebuchte Umsätze" + separator));
        {
            final var index = new AtomicInteger();
            final var required = matches.values().stream().map(item -> item.token).collect(Collectors.toCollection(ArrayList::new));
            final var allFound = lines.stream().anyMatch(line ->
            {
                final var currentIndex = index.incrementAndGet();
                final var match = matches.values().stream().filter(item -> item.matches(line)).findFirst();
                if (match.isPresent()) {
                    required.remove(match.get().token);
                    match.get().set(currentIndex, line);
                }
                return required.isEmpty();
            });
            if (!allFound) {
                throw new IllegalArgumentException("Required lines not found, probably file not matching: " + required);
            }
        }
        final var name = matches.get(Field.NAME).line.split(";")[1];
        final var namePattern = "([/])?" + name + "([/])?";
        final var iban = matches.get(Field.IBAN).line.split(";")[1];
        IBANValidator.validOrThrow(iban);

        return () -> {
            final var pending = lines.stream()
                    .skip(matches.get(Field.PENDING).index)
                    .takeWhile(Predicate.not(String::isEmpty))
                    .collect(Collectors.joining("\n"));
            final var transactions = new ArrayList<>(new CSVTransactionReader<>(new CsvToBeanBuilder<PostbankTransactionEntry>(new InputStreamReader(new ByteArrayInputStream(pending.getBytes())))
                    .withSeparator(separator)
                    .withType(PostbankTransactionEntry.class)
                    .build(), transaction ->
            {
                transaction.setRecipientOrPayer(transaction.getRecipientOrPayer().replaceFirst(namePattern, ""));
                transaction.setState(Transaction.State.PENDING);
                return transaction;
            }).read());

            final var booked = lines.stream()
                    .skip(matches.get(Field.BOOKED).index)
                    .takeWhile(Predicate.not(String::isEmpty))
                    .collect(Collectors.joining("\n"));
            transactions.addAll(new CSVTransactionReader<>(new CsvToBeanBuilder<PostbankTransactionEntry>(new InputStreamReader(new ByteArrayInputStream(booked.getBytes())))
                    .withSeparator(separator)
                    .withType(PostbankTransactionEntry.class)
                    .build(), transaction ->
            {
                transaction.setRecipientOrPayer(transaction.getRecipientOrPayer().replaceFirst(namePattern, ""));
                transaction.setState(Transaction.State.BOOKED);
                return transaction;
            }).read());

            return transactions;
        };
    }
}
