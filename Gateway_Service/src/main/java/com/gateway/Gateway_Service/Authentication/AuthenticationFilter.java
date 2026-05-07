package com.gateway.Gateway_Service.Authentication;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public AuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.equals("/users/login") || (path.equals("/users") && request.getMethod().equals("POST"))) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || authHeader.isBlank()) {
            writeUnauthorizedResponse(response, "TOKEN_MISSING", "Falta el token JWT");
            return;
        }

        if (!authHeader.startsWith("Bearer ")) {
            writeUnauthorizedResponse(response, "TOKEN_INVALID_FORMAT", "El token debe enviarse con el formato: Bearer <token>");
            return;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtUtil.validateToken(token);
            request.setAttribute("X-User-Id", claims.getSubject());
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            writeUnauthorizedResponse(response, "TOKEN_EXPIRED", "Token JWT expirado");

        } catch (JwtException | IllegalArgumentException e) {
            writeUnauthorizedResponse(response, "TOKEN_INVALID", "Token JWT inválido");
        }
    }

    private void writeUnauthorizedResponse(HttpServletResponse response,
            String error,
            String message) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("""
                {
                  "error": "%s",
                  "message": "%s"
                }
                """.formatted(error, message));
    }
}
