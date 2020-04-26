package com.runcita.api.runningType;

import com.runcita.api.shared.models.RunningType;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

/**
 * Running level repository
 */
public interface RunningTypeRepository extends Neo4jRepository<RunningType, Long> {

    @Override
    List<RunningType> findAll();
}
