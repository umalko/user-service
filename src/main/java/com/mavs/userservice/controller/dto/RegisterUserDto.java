package com.mavs.userservice.controller.dto;

import com.mavs.userservice.validation.PasswordMatches;
import com.mavs.userservice.validation.ValidEmail;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@PasswordMatches
public class RegisterUserDto {

    @NotNull
    @NotEmpty
    private String username;

    @NotNull
    @NotEmpty
    @ValidEmail
    private String email;

    @NotNull
    @NotEmpty
    private String password;

    @NotNull
    @NotEmpty
    private String confirmPassword;
}
