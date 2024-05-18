package com.example.mcvp.exceptions;

public class InternalException extends RuntimeException {
  public InternalException(Exceptions exception) {
    super(exception.getMessage());
  }
}
