
package com.hrms.core.exception.security;

import com.hrms.core.exception.BaseException;

public abstract class AppSecurityException extends BaseException {
    protected AppSecurityException(String message, int httpStatus) {
        super(message, httpStatus);
    }
}
