package com.Elecciones.elections.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Vote
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "voter_id")
    private UserApp voter;
    
    @ManyToOne
    @JoinColumn(name = "option_id")
    private Option option;
    private LocalDateTime votedAt;
}