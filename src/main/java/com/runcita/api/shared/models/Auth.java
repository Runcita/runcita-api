package com.runcita.api.shared.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class Auth {

    @NotNull
    private String email;
    @NotNull
    private String password;
}
