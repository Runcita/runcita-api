package com.runcita.api.shared.models;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
public class NewPassword {

    @NotNull
    @Size(min = 8, max = 100)
    private String oldPassword;

    @NotNull
    @Size(min = 8, max = 100)
    private String newPassword;
}
