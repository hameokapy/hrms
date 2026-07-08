
package com.hrms.core.exception.technical;

import com.hrms.core.exception.BaseException;

public abstract class TechnicalException extends BaseException {
    protected TechnicalException(String message, Throwable cause, int httpStatus) {
        super(message, cause, httpStatus);
    }
}
