package com.template.user.models;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SuccessResponse {
    protected final HttpStatus status = HttpStatus.OK;
    protected final String message;

    public SuccessResponse(String message) {
        this.message = message;
    }
}
