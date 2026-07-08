
package com.hrms.core.exception.business;

import com.hrms.core.exception.BaseException;

public abstract class BusinessException extends BaseException {
    
    protected BusinessException(String message, int httpStatus) {
        super(message, httpStatus);
    }
}