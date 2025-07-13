package com.Elecciones.elections.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record VotingEventInput(
        @NotBlank String title,
        @NotNull String description,
        @NotBlank LocalDateTime startTime,
        @NotBlank  LocalDateTime endTime)
{
}
