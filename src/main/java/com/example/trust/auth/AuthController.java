package com.example.trust.auth;

import com.example.trust.auth.dto.AuthResponse;
import com.example.trust.auth.dto.LoginRequest;
import com.example.trust.auth.dto.MeResponse;
import com.example.trust.auth.dto.RegisterRequest;
import com.example.trust.security.SecurityUtils;
import com.example.trust.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;
    private final UserRepository users;

    public AuthController(AuthService service,UserRepository users){
        this.service=service;
        this.users=users;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest req){
        service.register(req);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req){
        return service.login(req);
    }

    @GetMapping("/me")
    public MeResponse me(){
        int userId= SecurityUtils.requiredUserId();
        var profile=users.findProfileById(userId).orElseThrow();
        return new MeResponse(profile.id(),profile.email(),profile.fullName(),profile.roles());
    }
}
