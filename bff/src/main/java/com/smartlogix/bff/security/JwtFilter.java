package com.smartlogix.bff.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final String SECRET = "12345678901234567890123456789012";
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        // 🔥 permitir CORS preflight
        if (request.getMethod().equals("OPTIONS")) {
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        // 🔥 SOLO valida si viene token
        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                request.setAttribute("rol", claims.get("rol"));

            } catch (Exception e) {
                response.setStatus(403);
                return;
            }
        }

        // 🔥 deja pasar siempre
        chain.doFilter(request, response);
    }
}