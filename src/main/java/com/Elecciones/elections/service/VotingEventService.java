package com.Elecciones.elections.service;

import com.Elecciones.elections.domain.UserApp;
import com.Elecciones.elections.domain.VotingEvent;
import com.Elecciones.elections.repository.UserAppRepository;
import com.Elecciones.elections.repository.VotingEventRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VotingEventService
{
    private final VotingEventRepository votingEventRepository;
    private final UserAppRepository userAppRepository;
    private final Logger log = LoggerFactory.getLogger(VotingEventService.class);
    
    public VotingEventService(VotingEventRepository votingEventRepository, UserAppRepository userAppRepository) {
        this.votingEventRepository = votingEventRepository;
        this.userAppRepository = userAppRepository;
    }
    
    public Iterable<VotingEvent> getAllVotingEvents() {
        this.log.info("Get all voting events");
        return this.votingEventRepository.findAll();
    }
    
    private VotingEvent existVotingEventById(String id) {
        VotingEvent event = this.votingEventRepository.findById(id).orElse(null);
        if (event == null) {
            throw new RuntimeException("VotingEvent does not exist with ID: " + id);
        }
        return event;
    }
    
    public VotingEvent getVotingEventById(String id) {
        return this.existVotingEventById(id);
    }
    
    public VotingEvent createVotingEvent(VotingEvent votingEvent, String creatorId) {
        this.log.info("Create voting event with title: {}", votingEvent.getTitle());
        
        if (votingEvent.getStartTime() != null && votingEvent.getEndTime() != null &&
                votingEvent.getStartTime().isAfter(votingEvent.getEndTime())) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        }
        
        UserApp creator = userAppRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Creator user not found with ID: " + creatorId));
        
        votingEvent.setId(java.util.UUID.randomUUID().toString());
        votingEvent.setCreatedAt(LocalDateTime.now());
        votingEvent.setCreator(creator);
        
        return this.votingEventRepository.save(votingEvent);
    }
    
    public VotingEvent patchVotingEvent(String id, VotingEvent patch) {
        VotingEvent event = this.existVotingEventById(id);
        
        if (patch.getTitle() != null) {
            event.setTitle(patch.getTitle());
        }
        if (patch.getDescription() != null) {
            event.setDescription(patch.getDescription());
        }
        if (patch.getStartTime() != null) {
            event.setStartTime(patch.getStartTime());
        }
        if (patch.getEndTime() != null) {
            event.setEndTime(patch.getEndTime());
        }
        
        if (event.getStartTime() != null && event.getEndTime() != null &&
                event.getStartTime().isAfter(event.getEndTime())) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        }
        
        return this.votingEventRepository.save(event);
    }
    
    public VotingEvent putVotingEvent(String id, VotingEvent putVotingEvent, String creatorId) {
        VotingEvent event = this.existVotingEventById(id);
        
        UserApp creator = userAppRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Creator user not found with ID: " + creatorId));
        
        putVotingEvent.setId(id);
        putVotingEvent.setCreatedAt(event.getCreatedAt());
        putVotingEvent.setCreator(creator);
        
        if (putVotingEvent.getStartTime() != null && putVotingEvent.getEndTime() != null &&
                putVotingEvent.getStartTime().isAfter(putVotingEvent.getEndTime())) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        }
        
        return this.votingEventRepository.save(putVotingEvent);
    }
    
    public void deleteVotingEvent(String id) {
        VotingEvent event = this.existVotingEventById(id);
        this.votingEventRepository.delete(event);
        this.log.info("Deleted voting event with id {}", id);
    }
}
