package com.Elecciones.elections.dto;

import com.Elecciones.elections.domain.Status;

public record ParticipantOut(Long id, Status status, String userId, String name, String votingId, String votingTitle)
{
}
