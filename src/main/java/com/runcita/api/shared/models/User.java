package com.runcita.api.shared.models;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Builder;
import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.*;
import java.util.Date;

@Data
@NodeEntity
@Builder
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Size(min = 2, max = 20)
    @Pattern(regexp = "[A-Za-zÀ-ÖØ-öø-ÿ-]+")
    private String firstName;

    @NotNull
    @Size(min = 2, max = 20)
    @Pattern(regexp = "[A-Za-zÀ-ÖØ-öø-ÿ-]+")
    private String lastName;

    @NotNull
    @Pattern(regexp = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$")
    private String email;

    @NotNull
    @Size(min = 8, max = 100)
    private String password;

    @Max(300)
    private String description;

    private String cover;

    private String picture;

    @NotNull
    private Boolean sexe;

    @Relationship(type = "TO_HAVE")
    private RunningLevel runningLevel;

    @NotNull
    private Long birthday;

    @NotNull
    @Pattern(regexp = "[a-zA-Z]+")
    private String city;
}
