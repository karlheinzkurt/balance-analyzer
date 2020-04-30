package org.insaneheadoflettuce.input.csv.lbb;

import com.opencsv.bean.AbstractBeanField;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LBBDateConverter extends AbstractBeanField
{
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy", Locale.forLanguageTag("de_DE"));

    @Override
    protected Object convert(String value)
    {
        return LocalDate.parse(value, formatter);
    }
}
