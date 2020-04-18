package com.runcita.api.shared.models;

import lombok.Builder;
import lombok.Data;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NodeEntity
@Builder
public class City {

    @NotNull
    @Pattern(regexp = "[a-zA-Z]+")
    private String name;

    @Id
    @NotNull
    @Size(min = 5, max = 5)
    private Integer code;
}
