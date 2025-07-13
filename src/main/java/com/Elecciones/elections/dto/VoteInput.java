package com.Elecciones.elections.dto;

import jakarta.validation.constraints.NotBlank;

public record VoteInput(
        @NotBlank  Long optionId,
        @NotBlank  String userId
)
{
}
