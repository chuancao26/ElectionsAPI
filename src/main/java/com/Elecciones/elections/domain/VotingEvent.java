package com.Elecciones.elections.domain;

import com.Elecciones.elections.dto.VotingEventInput;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class VotingEvent
{
    @Id
    private String id;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    @JsonBackReference
    private UserApp creator;
    
    @OneToMany(mappedBy = "votingEvent", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Option> options = new ArrayList<>();
    private VotingEventStatus status = VotingEventStatus.OPENED;
    
    @OneToMany(mappedBy = "votingEvent", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Participant> participants = new ArrayList<>();
    
    public VotingEvent(VotingEventInput votingEventInput)
    {
        this.title = votingEventInput.title();
        this.description = votingEventInput.description();
        this.startTime = votingEventInput.startTime();
        this.endTime = votingEventInput.endTime();
    }
}
