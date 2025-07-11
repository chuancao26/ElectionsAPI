package com.Elecciones.elections.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleTokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("id_token") String idToken,
        @JsonProperty("expires_in") int expiresIn,
        @JsonProperty("token_type") String tokenType
) {}