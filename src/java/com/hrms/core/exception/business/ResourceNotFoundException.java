
package com.hrms.core.exception.business;

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String message) {
        super(message, 404);
    }
}

