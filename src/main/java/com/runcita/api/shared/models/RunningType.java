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
public class RunningType {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private RunningTypeEnum name;

    public enum RunningTypeEnum {
        FOOTING,
        FRACTIONNE,
        SORTIE_LONG,
        CINQ_KM,
        DIX_KM,
        SEMI_MARATHON,
        MARATHON,
        TRAIL
    }
}
