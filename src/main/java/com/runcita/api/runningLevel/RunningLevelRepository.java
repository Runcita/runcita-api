package com.runcita.api.runningLevel;

import com.runcita.api.shared.models.RunningLevel;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

/**
 * Running level repository
 */
public interface RunningLevelRepository extends Neo4jRepository<RunningLevel, Long> {

    @Override
    List<RunningLevel> findAll();
}
