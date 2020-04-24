package com.runcita.api.shared.models;

import lombok.Builder;
import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import javax.validation.constraints.NotNull;

@Data
@NodeEntity
@Builder
public class RunningLevel {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private RunningLevelEnum name;

    public enum RunningLevelEnum {
        TORTUE,
        GAZELLE,
        LAPIN,
        LEOPARD
    }
}
