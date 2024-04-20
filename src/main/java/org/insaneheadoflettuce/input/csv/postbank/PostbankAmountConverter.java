package org.insaneheadoflettuce.input.csv.postbank;

import com.opencsv.bean.AbstractBeanField;

public class PostbankAmountConverter extends AbstractBeanField {
    @Override
    protected Object convert(String value) {
        final var parts = value.split("\\s");
        if (parts[1].compareTo("€") != 0) {
            throw new IllegalStateException("€ sign missing: " + value);
        }
        return Double.valueOf(parts[0].trim().replaceAll("[.]", "").replaceAll("[,]", "."));
    }
}
