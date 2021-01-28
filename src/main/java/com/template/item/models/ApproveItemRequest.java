package com.template.item.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@ApiModel
public class ApproveItemRequest {
    @NotNull(message = "Item ID must not be null")
    @ApiModelProperty(
            value = "Item id",
            name = "id",
            example = "1",
            required = true)
    private Long id;

    @NotNull(message = "isApproved must not be null ")
    @ApiModelProperty(
            value = "Is approved",
            name = "isApproved",
            example = "true",
            required = true)
    private Boolean isApproved;
}
