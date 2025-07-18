package com.Elecciones.elections.domain;

import com.Elecciones.elections.dto.UserInput;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserApp
{
    @Id
    private String id;
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    private String photo;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime modifiedAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<VotingEvent> votingEvents = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Participant> eventParticipant= new ArrayList<>();
    
    public UserApp(UserInput userInput)
    {
        this.setId(userInput.id());
        this.setName(userInput.name());
        this.setEmail(userInput.email());
        this.setPhoto(userInput.photo());
    }
}
