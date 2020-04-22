package com.runcita.api.user;

import com.runcita.api.shared.models.User;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Auth repository
 */
public interface UserRepository extends Neo4jRepository<User, Long> {

    @Query("MATCH (user:User)<-[:TO_LINK]-(auth:Auth) " +
            "WHERE ID(user)={userId} " +
            "RETURN auth.email")
    String findEmailUser(@Param("userId") Long userId);

    @Query("MATCH (user:User), (subscriber:User) " +
            "WHERE ID(user)={userId} AND ID(subscriber)={otherUserId} " +
            "CREATE (user)-[:TO_FOLLOW]->(subscriber)")
    void subscribeUser(@Param("userId") Long userId, @Param("otherUserId") Long otherUserId);

    @Query("MATCH (user:User)-[:TO_FOLLOW]->(subscriber:User) " +
            "WHERE ID(user)={userId} AND ID(subscriber)={otherUserId} " +
            "RETURN count(user) > 0")
    boolean subscriptionUserExists(@Param("userId") Long userId, @Param("otherUserId") Long otherUserId);

    @Query("MATCH (user:User)-[:TO_FOLLOW]->(subscription:User) " +
            "WHERE ID(user)={userId} " +
            "RETURN subscription")
    List<User> findSubscriptionsOfUser(@Param("userId") Long userId);

    @Query("MATCH (user:User)<-[:TO_FOLLOW]-(subscriber:User) " +
            "WHERE ID(user)={userId} " +
            "RETURN subscriber")
    List<User> findSubscribersOfUser(@Param("userId") Long userId);
}
