package org.insaneheadoflettuce.input.csv.lbb;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import org.insaneheadoflettuce.balanceAnalyzer.model.Transaction;
import org.insaneheadoflettuce.balanceAnalyzer.utility.IBANValidator;
import org.insaneheadoflettuce.input.csv.CSVTransactionEntry;

import java.time.LocalDate;

public class LBBTransactionEntry implements CSVTransactionEntry
{
    private static final String defaultLocale = "de-DE";

    @CsvBindByName(column = "Auftragskonto", locale = defaultLocale, required = true)
    String auftragsKonto;

    @CsvCustomBindByName(column = "Buchungstag", converter = LBBDateConverter.class, required = true)
    LocalDate buchungsDatum;

    @CsvCustomBindByName(column = "Valutadatum", converter = LBBDateConverter.class, required = true)
    LocalDate wertstellungsDatum;

    @CsvBindByName(column = "Buchungstext", locale = defaultLocale, required = true)
    String buchungsText;

    @CsvBindByName(column = "Verwendungszweck", locale = defaultLocale)
    String verwendungsZweck;

    @CsvBindByName(column = "Glaeubiger ID", locale = defaultLocale)
    String glaubigerId;

    @CsvBindByName(column = "Mandatsreferenz", locale = defaultLocale)
    String mandatsReferenz;

    @CsvBindByName(column = "Kundenreferenz (End-to-End)", locale = defaultLocale)
    String kundenReferenz;

    @CsvBindByName(column = "Sammlerreferenz", locale = defaultLocale)
    String sammlerReferenz;

    @CsvBindByName(column = "Lastschrift Ursprungsbetrag", locale = defaultLocale)
    String lastschriftUrsprungsbetrag;

    @CsvBindByName(column = "Auslagenersatz Ruecklastschrift", locale = defaultLocale)
    String auslagenersatzRuecklastschrift;

    @CsvBindByName(column = "Beguenstigter/Zahlungspflichtiger", locale = defaultLocale)
    String beguenstigterZahlungspflichtiger;

    @CsvBindByName(column = "Kontonummer/IBAN", locale = defaultLocale)
    String beguenstigterKonto;

    @CsvBindByName(column = "BIC (SWIFT-Code)", locale = defaultLocale)
    String beguenstigterBIC;

    @CsvBindByName(column = "Betrag", locale = defaultLocale, required = true)
    Double betrag;

    @CsvBindByName(column = "Waehrung", locale = defaultLocale, required = true)
    String waehrung;

    @CsvCustomBindByName(column = "Info", converter = LBBInformationConverter.class, required = true)
    Transaction.State state;

    @Override
    public Transaction toTransaction()
    {
        IBANValidator.validOrThrow(auftragsKonto);

        final var t = new Transaction();
        t.setValueDate(wertstellungsDatum);
        t.setPostingText(buchungsText);
        t.setPurpose(verwendungsZweck);
        t.setRecipientOrPayer(beguenstigterZahlungspflichtiger);
        t.setAmount(betrag);
        t.setState(state);
        return t;
    }
}
