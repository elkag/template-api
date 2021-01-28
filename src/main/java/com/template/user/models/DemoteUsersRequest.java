package com.template.user.models;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class DemoteUsersRequest {
    @NotNull(message = "User ID must not be null")
    private Long id;

    @NotNull(message = "isDemoted must not be null ")
    private Boolean isDemoted;
}
