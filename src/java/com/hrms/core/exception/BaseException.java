
package com.hrms.core.exception;

public abstract class BaseException extends RuntimeException {
    private final int httpStatus;

    protected BaseException(String message, int httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
    
    protected BaseException(String message, Throwable cause, int httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
