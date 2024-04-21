package org.insaneheadoflettuce.balance_analyzer.dao;

import org.insaneheadoflettuce.balance_analyzer.model.ClusterDescription;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClusterDescriptionRepository extends CrudRepository<ClusterDescription, Long> {
}
