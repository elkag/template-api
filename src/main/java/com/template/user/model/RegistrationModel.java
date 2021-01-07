package com.template.user.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldMatch(
        first = "password",
        second = "repeatPassword",
        message = "Password do not match")
//@RoleMatch(role = "role", message = "Invalid user role")
public class RegistrationModel {

    @NotNull
    @Email
    private String email;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String password;

    @NotNull
    private String repeatPassword;
}
