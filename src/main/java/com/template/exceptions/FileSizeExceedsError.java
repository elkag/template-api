package com.template.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class FileSizeExceedsError extends Error {

    private final long maxFileSize = 5242880;

    public FileSizeExceedsError(HttpStatus status, String message, List<String> errors) {
        super(status, message, errors);
    }

    public FileSizeExceedsError(HttpStatus status, String message, String error) {
        super(status, message, error);
    }
}
