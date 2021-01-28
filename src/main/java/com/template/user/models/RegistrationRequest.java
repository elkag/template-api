package com.template.user.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldMatch(
        first = "password",
        second = "repeatPassword",
        message = "Passwords do not match.")
public class RegistrationRequest {

    @NotNull(message = "Email must not be null.")
    @NotEmpty(message = "Email must not be empty.")
    @Email(message = "Incorrect email.")
    private String email;

    @NotNull(message = "First name must not be null.")
    @NotEmpty(message = "First name must not be empty.")
    private String firstName;

    @NotNull(message = "Last name must not be null.")
    @NotEmpty(message = "Last name must not be empty.")
    private String lastName;

    @NotNull(message = "Password must not be null.")
    @NotEmpty(message = "Password must not be empty.")
    private String password;

    @NotNull(message = "Re-entered password must not be null.")
    @NotEmpty(message = "Re-entered password must not be empty.")
    private String repeatPassword;
}
