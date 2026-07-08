
package com.hrms.core.security;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordHash {
    
    // vào trang https://bcrypt-generator.com/ generate/decode hashed password
    
    private PasswordHash() {}

    public static String hashPassword(String password) {
        if (password == null) 
            return null;
        return BCrypt.withDefaults().hashToString(10, password.toCharArray());
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) 
            return false;
        BCrypt.Result result = BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword);
        return result.verified;
    }
}
