package org.insaneheadoflettuce.balanceAnalyzer.dao;

import jakarta.persistence.EntityManager;
import org.insaneheadoflettuce.balanceAnalyzer.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Unfortunately, this has to be named "...Impl" to work with spring mechanics.
 */
@Repository
public class TransactionRepositoryImpl implements TransactionRepositoryCustom {
    @Autowired
    @Lazy
    TransactionRepository repository;

    @Autowired
    EntityManager entityManager;

    @Override
    public List<Transaction> findByMonth(LocalDate date) {
        return repository.findByMonth(date.getMonthValue(), date.getYear());
    }

    @Override
    public List<Transaction> findByYear(LocalDate date) {
        return repository.findByYear(date.getYear());
    }

    @Override
    public Optional<Pair<LocalDate, LocalDate>> getRange() {
        final var latestTransaction = repository.getFirstByOrderByValueDateDesc();
        final var earliestTransaction = repository.getFirstByOrderByValueDateAsc();
        if (latestTransaction.isEmpty() || earliestTransaction.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Pair.of(earliestTransaction.get().getValueDate(), latestTransaction.get().getValueDate()));
    }

    @Override
    public Optional<Pair<LocalDate, LocalDate>> getRangeByAccount(Long accountId) {

        final var latestTransaction = repository.getFirstByAccountIdOrderByValueDateDesc(accountId);
        final var earliestTransaction = repository.getFirstByAccountIdOrderByValueDateAsc(accountId);
        if (latestTransaction.isEmpty() || earliestTransaction.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Pair.of(earliestTransaction.get().getValueDate(), latestTransaction.get().getValueDate()));
    }

    @Override
    public String getRangeByAccountAsString(Long accountId) {
        final var range = getRangeByAccount(accountId).orElse(Pair.of(LocalDate.MIN, LocalDate.MAX));
        return String.format("%s - %s", range.getFirst().toString(), range.getSecond().toString());
    }

    /*@Override
    public Optional<Transaction> findYoungestBookedTransaction(Long accountId)
    {
        final List<Transaction> results = entityManager.createQuery("select t from Transaction t inner join t.account a where a.id = :accountId and t.state = :state order by t.valueDate desc")
                .setParameter("accountId", accountId)
                .setParameter("state", Transaction.State.BOOKED)
                .setMaxResults(1)
                .getResultList();
        return results.stream().collect(MoreCollectors.toOptional());
    }*/
}
