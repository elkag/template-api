package com.template.item.models;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class ApproveItemRequest {
    @NotNull(message = "Item ID must not be null")
    private Long id;

    @NotNull(message = "isApproved must not be null ")
    private Boolean isApproved;
}
