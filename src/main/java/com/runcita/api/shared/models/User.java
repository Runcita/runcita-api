package com.runcita.api.shared.models;

import lombok.Builder;
import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

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
    @Relationship(type = "TO_LIVE")
    private City city;
}
