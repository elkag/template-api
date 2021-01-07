package com.template.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Getter
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class HttpBadRequestException extends RuntimeException {

  private List<String> errors;

  public HttpBadRequestException(List<String> errors, String message) {
    super(message);
    this.errors = errors;
  }

  public HttpBadRequestException(String message) {
    super(message);
  }
}
