package org.insaneheadoflettuce.input.csv.postbank;

import com.opencsv.bean.AbstractBeanField;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class PostbankDateConverter extends AbstractBeanField {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.forLanguageTag("de_DE"));

    @Override
    protected Object convert(String value) {
        return LocalDate.parse(value, formatter);
    }
}
