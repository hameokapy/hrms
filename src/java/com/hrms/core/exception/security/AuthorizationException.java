
package com.hrms.core.exception.security;

public class AuthorizationException extends AppSecurityException {
    public AuthorizationException(String message) {
        super(message, 403); 
    }
}
