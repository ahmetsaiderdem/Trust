package com.example.trust.auth.dto;

import java.util.List;

public class AuthResponse {

    private String token;
    private int userId;
    private String email;
    private List<String> roles;

    public AuthResponse(String token, int userId,String email,List<String> roles){
        this.token=token;
        this.userId=userId;
        this.email=email;
        this.roles=roles;

    }

    public String getToken() { return token; }
    public int getUserId() { return userId; }
    public String getEmail() { return email; }
    public List<String> getRoles() { return roles; }
}
