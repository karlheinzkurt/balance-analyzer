package org.insaneheadoflettuce.balanceAnalyzer.dao;

import org.insaneheadoflettuce.balanceAnalyzer.model.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long>, TransactionRepositoryCustom
{
    List<Transaction> getAllByPurposeLike(String purpose);

    Optional<Transaction> getFirstByStateOrderByValueDateDesc(Transaction.State state);

    Optional<Transaction> getFirstByStateOrderByValueDateAsc(Transaction.State state);

    Optional<Transaction> getFirstByOrderByValueDateDesc();

    Optional<Transaction> getFirstByOrderByValueDateAsc();

    @Query("select t from Transaction t where MONTH(t.valueDate) = :month and YEAR(t.valueDate) = :year")
    List<Transaction> findByMonth(@Param("month") int month, @Param("year") int year);

    @Query("select t from Transaction t where YEAR(t.valueDate) = :year")
    List<Transaction> findByYear(@Param("year") int year);

    List<Transaction> getAllByOrderByValueDateDesc();

    List<Transaction> getAllByAccountIdOrderByValueDateDesc(Long accountId);

    Optional<Transaction> getFirstByAccountIdOrderByValueDateDesc(Long accountId);

    Optional<Transaction> getFirstByAccountIdOrderByValueDateAsc(Long accountId);
}
