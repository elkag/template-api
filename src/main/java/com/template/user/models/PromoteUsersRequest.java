package com.template.user.models;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class PromoteUsersRequest {
    @NotNull(message = "User ID must not be null")
    private Long id;

    @NotNull(message = "isPromoted must not be null ")
    private Boolean isPromoted;
}
