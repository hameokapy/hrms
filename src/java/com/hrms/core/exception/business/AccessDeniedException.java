
package com.hrms.core.exception.business;

public class AccessDeniedException extends BusinessException {
    public AccessDeniedException(String message) {
        super(message, 403);
    }
}
