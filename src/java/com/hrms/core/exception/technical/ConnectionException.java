
package com.hrms.core.exception.technical;

public class ConnectionException extends TechnicalException {
    public ConnectionException(String message, Throwable cause) {
        super(message, cause, 500);
    }
}
