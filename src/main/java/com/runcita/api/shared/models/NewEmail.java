package com.runcita.api.shared.models;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
public class NewEmail {

    @NotNull
    @Size(min = 8, max = 100)
    private String password;

    @NotNull
    @Pattern(regexp = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$")
    private String newEmail;
}
