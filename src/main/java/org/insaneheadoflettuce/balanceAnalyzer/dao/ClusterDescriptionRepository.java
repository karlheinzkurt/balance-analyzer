package org.insaneheadoflettuce.balanceAnalyzer.dao;

import org.insaneheadoflettuce.balanceAnalyzer.model.ClusterDescription;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClusterDescriptionRepository extends CrudRepository<ClusterDescription, Long>
{
}
