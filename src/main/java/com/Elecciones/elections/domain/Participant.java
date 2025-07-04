package com.Elecciones.elections.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Status status;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private UserApp participant;
    
    @ManyToOne
    @JoinColumn(name = "voting_event_id")
    @JsonBackReference
    private VotingEvent votingEvent;
}
