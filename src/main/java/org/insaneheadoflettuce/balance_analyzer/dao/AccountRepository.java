package org.insaneheadoflettuce.balance_analyzer.dao;

import org.insaneheadoflettuce.balance_analyzer.model.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {
    //@Query("select a.transactions from Transaction t inner join u.area ar where ar.idArea = :idArea")
    //List<Transaction> getFirst1Earliest();


}
