package com.Elecciones.elections.dto;

import com.Elecciones.elections.domain.VotingEventStatus;

import java.time.LocalDateTime;

public record VotingEventOut(String id,
                             String title,
                             String description,
                             LocalDateTime startTime,
                             LocalDateTime endTime,
                             String userId,
                             String userName,
                             VotingEventStatus status
                             )
{
}
