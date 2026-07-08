
package com.hrms.core.exception.business;

public class IllegalOperationException extends BusinessException {
    public IllegalOperationException(String message) {
        super(message, 400);
    }
}
