package org.insaneheadoflettuce.balance_analyzer.dao;

import org.insaneheadoflettuce.balance_analyzer.model.Transaction;
import org.springframework.data.util.Pair;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepositoryCustom {
    List<Transaction> findByMonth(LocalDate date);

    List<Transaction> findByYear(LocalDate date);

    Optional<Pair<LocalDate, LocalDate>> getRange();

    Optional<Pair<LocalDate, LocalDate>> getRangeByAccount(Long accountId);

    String getRangeByAccountAsString(Long accountId);
}
