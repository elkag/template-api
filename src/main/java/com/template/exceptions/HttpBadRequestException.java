package com.template.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason="To show an example of a custom message")
public class HttpBadRequestException extends RuntimeException {


  public HttpBadRequestException(String message) {
    super(message);
  }
}
