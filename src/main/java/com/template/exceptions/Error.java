package com.template.exceptions;

public class Error {
    public String errorName;
    public String errorDescription;

    public String getErrorName() {
        return errorName;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public Error setErrorName(String errorName) {
        this.errorName = errorName;
        return this;
    }

    public Error setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
        return this;
    }
}
