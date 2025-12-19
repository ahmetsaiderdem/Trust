package com.example.trust.auth.dto;

import java.util.List;

public class MeResponse {

    private int id;
    private String email;
    private String fullName;
    private List<String> roles;

    public MeResponse(int id, String email,String fullName,List<String> roles){
        this.id=id;
        this.email=email;
        this.fullName=fullName;
        this.roles=roles;


    }
    public int getId() { return id; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public List<String> getRoles() { return roles; }
}
