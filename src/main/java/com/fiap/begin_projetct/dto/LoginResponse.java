package com.fiap.begin_projetct.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String type;
    private String error;
    
    public LoginResponse(String token, String type) {
        this.token = token;
        this.type = type;
    }
}
