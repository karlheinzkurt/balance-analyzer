package org.insaneheadoflettuce.balance_analyzer;

import org.insaneheadoflettuce.balance_analyzer.model.Account;
import org.insaneheadoflettuce.balance_analyzer.model.Transaction;

import java.util.List;

public abstract class AbstractTransactionCollection implements TransactionCollection {

    @Override
    public final int getSize() {
        return getTransactions().size();
    }

    @Override
    public final List<String> getAccountColors() {
        return getTransactions().stream()
                .map(Transaction::getAccount)
                .distinct()
                .map(Account::getColor)
                .toList();
    }

    @Override
    public final Number getDifferentialMovement() {
        return new Number(getTransactions().stream()
                .map(Transaction::getAmount)
                .mapToDouble(Number::getValue)
                .sum());
    }

    @Override
    public final Number getAbsoluteMovement() {
        return new Number(getTransactions().stream()
                .map(Transaction::getAmount)
                .mapToDouble(a -> Math.abs(a.getValue()))
                .sum(), true);
    }

    @Override
    public final Number getPositiveMovement() {
        return new Number(getTransactions().stream()
                .map(Transaction::getAmount)
                .filter(Number::isPositive)
                .mapToDouble(Number::getValue)
                .sum());
    }

    @Override
    public final Number getNegativeMovement() {
        return new Number(getTransactions().stream()
                .map(Transaction::getAmount)
                .filter(Number::isNegative)
                .mapToDouble(Number::getValue)
                .sum());
    }
}
