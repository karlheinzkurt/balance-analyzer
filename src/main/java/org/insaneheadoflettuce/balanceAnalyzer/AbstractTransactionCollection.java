package org.insaneheadoflettuce.balanceAnalyzer;

import org.insaneheadoflettuce.balanceAnalyzer.model.Account;
import org.insaneheadoflettuce.balanceAnalyzer.model.Transaction;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractTransactionCollection implements TransactionCollection
{
    @Override
    final public int getSize()
    {
        return getTransactions().size();
    }

    @Override
    final public List<String> getAccountColors()
    {
        return getTransactions().stream()
                .map(Transaction::getAccount)
                .distinct()
                .map(Account::getColor)
                .collect(Collectors.toList());
    }

    @Override
    final public Number getDifferentialMovement()
    {
        return new Number(getTransactions().stream()
                .map(Transaction::getAmount)
                .mapToDouble(Number::getValue)
                .sum());
    }

    @Override
    final public Number getAbsoluteMovement()
    {
        return new Number(getTransactions().stream()
                .map(Transaction::getAmount)
                .mapToDouble(a -> Math.abs(a.getValue()))
                .sum(), true);
    }

    @Override
    final public Number getPositiveMovement()
    {
        return new Number(getTransactions().stream()
                .map(Transaction::getAmount)
                .filter(Number::isPositive)
                .mapToDouble(Number::getValue)
                .sum());
    }

    @Override
    final public Number getNegativeMovement()
    {
        return new Number(getTransactions().stream()
                .map(Transaction::getAmount)
                .filter(Number::isNegative)
                .mapToDouble(Number::getValue)
                .sum());
    }
}
