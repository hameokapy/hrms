
package com.hrms.core.exception.technical;

public class DatabaseException extends TechnicalException {
    public DatabaseException(String message, Throwable cause) {
        super(message, cause, 500);
    }
}
