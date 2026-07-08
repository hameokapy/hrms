
package com.hrms.service;

import com.hrms.model.dto.response.LoginResponseDTO;

public interface AuthService {
    LoginResponseDTO login(String username, String password);
}
