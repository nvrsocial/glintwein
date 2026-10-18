package com.nvrsocial.glintwein.auth.dto;


import lombok.Getter;

@Getter
public class LoginResponse {
    private final String accessToken;
    private final String tokenType;

    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
    }
}
