
package com.hrms.core.exception.security;

public class InvalidCredentialsException extends AppSecurityException {
    public InvalidCredentialsException(String message) {
        super(message, 401); 
    }
}
