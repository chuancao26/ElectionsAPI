package com.Elecciones.elections.domain;

import com.Elecciones.elections.dto.OptionInput;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Option
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String label;
    private LocalDateTime createdAt = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name = "voting_event_id")
    private VotingEvent votingEvent;
    public Option(OptionInput optionInput)
    {
        this.label = optionInput.label();
    }
}
