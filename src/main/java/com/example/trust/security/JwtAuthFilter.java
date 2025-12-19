package com.example.trust.security;

import com.example.trust.auth.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwt;

    public JwtAuthFilter(JwtService jwt){
        this.jwt=jwt;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)throws ServletException, IOException{
        String header =req.getHeader("Authorization");
        if (header== null || !header.startsWith("Bearer")){
            chain.doFilter(req,res);
            return;
        }

        String token=header.substring("Bearer".length()).trim();
        try {
            Claims c= jwt.parse(token);
            int userId=Integer.parseInt(c.getSubject());
            String email=c.get("email",String.class);

            @SuppressWarnings("unchecked")
                    List<String> roles= (List<String>) c.get("roles");

            var authorities =roles.stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" +r))
                    .collect(Collectors.toList());

            var principal =new AuthUser(userId,email,authorities);
            var auth=new UsernamePasswordAuthenticationToken(principal,null,authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }catch (Exception e){
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(req,res);
    }
}
