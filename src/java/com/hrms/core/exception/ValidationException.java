
package com.hrms.core.exception;

import java.util.Map;

public class ValidationException extends BaseException {
    private final Map<String, String> errors;

    public ValidationException(Map<String, String> errors) {
        super("Validation failed:", 400);
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
    
}
