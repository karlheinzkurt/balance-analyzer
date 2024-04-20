package org.insaneheadoflettuce.balanceAnalyzer.dao;

import org.insaneheadoflettuce.balanceAnalyzer.model.Cluster;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClusterRepository extends CrudRepository<Cluster, Long> {
}
