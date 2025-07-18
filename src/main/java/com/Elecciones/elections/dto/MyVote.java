package com.Elecciones.elections.dto;

import java.time.LocalDateTime;

public record MyVote(String nameVotingEvent,
                     String optionName,
                     LocalDateTime votedTime) {
}
