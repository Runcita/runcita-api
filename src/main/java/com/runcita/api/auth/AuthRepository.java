package com.runcita.api.auth;

import com.runcita.api.shared.models.Auth;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

/**
 * Auth repository
 */
public interface AuthRepository extends Neo4jRepository<Auth, Long> {

    Optional<Auth> findByEmail(String email);

    boolean existsByEmail(String email);
}
