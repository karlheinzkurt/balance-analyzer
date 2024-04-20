package org.insaneheadoflettuce.input.csv.lbb;

import com.opencsv.bean.AbstractBeanField;
import org.insaneheadoflettuce.balanceAnalyzer.model.Transaction;

import java.util.Map;

public class LBBInformationConverter extends AbstractBeanField {
    final static Map<String, Transaction.State> string2State = Map.of(
            "umsatz vorgemerkt", Transaction.State.PENDING,
            "umsatz gebucht", Transaction.State.BOOKED
    );

    @Override
    protected Object convert(String value) {
        final var lower = value.toLowerCase();
        if (string2State.containsKey(lower)) {
            return string2State.get(lower);
        }
        return Transaction.State.UNDEFINED;
    }
}
