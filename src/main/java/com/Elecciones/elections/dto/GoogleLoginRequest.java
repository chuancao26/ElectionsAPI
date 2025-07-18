package com.Elecciones.elections.dto;

public record GoogleLoginRequest(
        String code,
        String codeVerifier,
        String redirectUri
) {}