package com.Proyect.UserService.config;

import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtServices {

    private static final String SECRET_KEY = "vcGaq5k1m0VMQrjqzNoCRtHhS/+HecujQ30kr8PfSXc=";

    public static String generateToken(String username) {
    return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 86400000))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact();
    }
}
