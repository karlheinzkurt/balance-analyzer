package org.insaneheadoflettuce.input.csv.postbank;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import org.insaneheadoflettuce.balance_analyzer.model.Transaction;
import org.insaneheadoflettuce.input.csv.CSVTransactionEntry;

import java.time.LocalDate;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PostbankTransactionEntry implements CSVTransactionEntry {
    private static final String defaultLocale = "Windows-1252";

    @CsvCustomBindByName(column = "Buchungsdatum", converter = PostbankDateConverter.class, required = true)
    LocalDate buchungsDatum;

    @CsvCustomBindByName(column = "Wertstellung", converter = PostbankDateConverter.class, required = false)
    LocalDate wertstellungsDatum;

    @CsvBindByName(column = "Umsatzart", locale = defaultLocale, required = true)
    String umsatzArt;

    @CsvBindByName(column = "Buchungsdetails", locale = defaultLocale)
    String verwendungsZweck;

    @CsvBindByName(column = "Auftraggeber", locale = defaultLocale)
    String auftraggeber;

    @CsvBindByName(column = "Empfänger", locale = defaultLocale)
    String empfaenger;

    @CsvCustomBindByName(column = "Betrag (€)", converter = PostbankAmountConverter.class, required = true)
    Double betrag;

    @CsvCustomBindByName(column = "Saldo (€)", converter = PostbankAmountConverter.class, required = false)
    Double saldo;

    @Override
    public Transaction toTransaction() {
        final var t = new Transaction();
        t.setValueDate(wertstellungsDatum != null ? wertstellungsDatum : buchungsDatum);
        t.setPostingText(umsatzArt);
        t.setPurpose(verwendungsZweck);
        t.setRecipientOrPayer(Stream.of(auftraggeber, empfaenger).filter(Objects::nonNull).filter(Predicate.not(String::isEmpty)).collect(Collectors.joining("/")));
        t.setAmount(betrag);
        return t;
    }
}
