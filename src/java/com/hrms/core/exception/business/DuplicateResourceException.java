
package com.hrms.core.exception.business;

public class DuplicateResourceException extends BusinessException {
    public DuplicateResourceException(String message) {
        super(message, 409);
    }
}
