package com.Elecciones.elections.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserInput(
        @NotBlank  String id,
        @NotBlank  String name,
        @Email  String email,
        String photo)
{
}
