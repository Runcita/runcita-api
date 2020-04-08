package com.runcita.api.user;

import com.runcita.api.shared.models.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;

/**
 * User repository
 */
public interface UserRepository extends Neo4jRepository<User, Long> {

    User findByEmail(String email);

    boolean existsByEmail(String email);
}
