package com.template.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Getter
public class FileSizeExceedsError extends Error {

    private final long maxFileSize = 5242880;

    public FileSizeExceedsError(HttpStatus status, String message, String error, HttpServletRequest request) {
        super(status, message, error, request);
    }
}
