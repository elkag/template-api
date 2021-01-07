package com.template.user.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginModel {

    @NotNull
    private String email;

    @NotNull
    private String password;
}
