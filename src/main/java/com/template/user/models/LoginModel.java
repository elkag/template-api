package com.template.user.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginModel {

    @NotNull(message = "Email must not be null.")
    @NotEmpty(message = "Email must not be empty.")
    @ApiModelProperty(
            value = "user's email (username)",
            name = "email",
            example = "admin@template.com",
            required = true)
    private String email;

    @NotNull(message = "Password must not be null.")
    @NotEmpty(message = "Password must not be empty.")
    @ApiModelProperty(
            value = "user's password",
            name = "password",
            required = true)
    private String password;
}
