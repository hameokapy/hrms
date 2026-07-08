
package com.hrms.core.exception.technical;

public class TransactionException extends TechnicalException {
    public TransactionException(String message, Throwable cause) {
        super(message, cause, 500);
    }
}
