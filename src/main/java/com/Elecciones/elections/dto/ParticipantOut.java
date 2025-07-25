package com.Elecciones.elections.dto;

import com.Elecciones.elections.domain.Status;
import com.Elecciones.elections.domain.VotingEventStatus;

public record ParticipantOut(Long id,
                             Status status,
                             String userId,
                             String name,
                             String votingId,
                             String votingTitle,
                             VotingEventStatus votingEventStatus,
                             String eventDescription)
    
{
}
