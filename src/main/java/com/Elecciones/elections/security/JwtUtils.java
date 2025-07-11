package com.Elecciones.elections.security;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.Map;

public class JwtUtils {
    
    public static Map<String, Object> decodeJWT(String jwt) {
        String[] parts = jwt.split("\\.");
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
        try {
            return new ObjectMapper().readValue(payload, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decode JWT", e);
        }
    }
}