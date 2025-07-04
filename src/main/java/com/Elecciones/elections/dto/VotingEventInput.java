package com.Elecciones.elections.dto;

import java.time.LocalDateTime;

public record VotingEventInput(String title,
                               String description,
                               LocalDateTime startTime,
                               LocalDateTime endTime)
{
}
