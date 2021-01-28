package com.template.user.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldMatch(
        first = "newPassword",
        second = "repeatPassword",
        message = "Your new password does not match the re-entered new password.")
@ApiModel
public class ChangePasswordRequest {

    @NotNull(message = "Password must not be null.")
    @NotEmpty(message = "Password must not be empty.")
    @ApiModelProperty(
            value = "user's old password",
            name = "password",
            example = "old_password",
            position = 1)
    private String password;

    @NotNull(message = "New password must not be null.")
    @NotEmpty(message = "New password must not be empty.")
    @ApiModelProperty(
            value = "user's new password",
            name = "newPassword",
            example = "super_strong_new_password",
            position = 2)
    private String newPassword;

    @NotNull(message = "Re-entered password must not be null.")
    @NotEmpty(message = "Re-entered password must not be empty.")
    @ApiModelProperty(
            value = "user's new password again",
            name = "repeatPassword",
            example = "super_strong_new_password",
            position = 3)
    private String repeatPassword;
}
