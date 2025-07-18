package com.Elecciones.elections.dto;

public record LoginResponse(
        String token,
        String userId,
        String email,
        String name
) {}