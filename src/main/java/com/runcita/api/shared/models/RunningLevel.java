package com.runcita.api.shared.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import javax.validation.constraints.NotNull;

@Data
@NodeEntity
@NoArgsConstructor
public class RunningLevel {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private RunningLevelEnum name;

    private enum RunningLevelEnum {
        TORTUE,
        GAZELLE,
        LAPIN,
        LEOPARD
    }
}
