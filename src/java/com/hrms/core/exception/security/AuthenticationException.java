
package com.hrms.core.exception.security;

public class AuthenticationException extends AppSecurityException {
    public AuthenticationException(String message) {
        super(message, 401);
    }
}
