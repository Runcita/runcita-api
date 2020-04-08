package com.runcita.api.shared.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import java.sql.Timestamp;

@Data
@Builder
@NodeEntity
public class User {

    @Id
    @GeneratedValue
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    @JsonIgnore
    private String password;
    private String description;
    private String picture;
    private Boolean sexe;
    private String runningLevel;
    private Timestamp birthday;
}
