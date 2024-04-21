package org.insaneheadoflettuce.balance_analyzer.dao;

import org.insaneheadoflettuce.balance_analyzer.model.Cluster;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClusterRepository extends CrudRepository<Cluster, Long> {
}
