package com.Elecciones.elections.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Participant
{
    @Id
    private Long id;
    private Status status;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserApp user;
    @ManyToOne
    @JoinColumn(name = "voting_event_id")
    private VotingEvent votingEvent;
}
