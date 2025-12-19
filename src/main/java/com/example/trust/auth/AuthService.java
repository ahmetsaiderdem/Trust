package com.example.trust.auth;

import com.example.trust.auth.dto.AuthResponse;
import com.example.trust.auth.dto.LoginRequest;
import com.example.trust.auth.dto.RegisterRequest;
import com.example.trust.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwt){
        this.users=users;
        this.encoder=encoder;
        this.jwt=jwt;
    }

    public void register(RegisterRequest req){
        if (users.existsByEmail(req.getEmail())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Bu email zaten kayıtlı");
        }

        String hash=encoder.encode(req.getPassword());
        int userId=users.createUser(req.getEmail(),hash,req.getFullName());
        users.addRole(userId,"USER");
    }

    public AuthResponse login(LoginRequest req){
        var u =users.findAuthByEmail(req.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Email veya şifre hatalı"));
        if (!u.enabled()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Hesap pasif");
        }

        if (!encoder.matches(req.getPassword(),u.passwordHash())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Email veya şifre hatalı");

        }

        String token=jwt.generate(u.id(),u.email(),u.roles());
        return new AuthResponse(token,u.id(),u.email(),u.roles());
    }

}
