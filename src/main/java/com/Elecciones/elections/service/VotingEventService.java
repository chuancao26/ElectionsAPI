package com.Elecciones.elections.service;

import com.Elecciones.elections.Exception.BadRequestException;
import com.Elecciones.elections.Exception.ConflictException;
import com.Elecciones.elections.domain.UserApp;
import com.Elecciones.elections.domain.VotingEvent;
import com.Elecciones.elections.dto.VotingEventInput;
import com.Elecciones.elections.dto.VotingEventOut;
import com.Elecciones.elections.repository.VotingEventRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VotingEventService
{
    private final VotingEventRepository votingEventRepository;
    private final UserAppService userAppService;
    private final Logger log = LoggerFactory.getLogger(VotingEventService.class);
    private final ServerProperties serverProperties;
    
    public VotingEventService(VotingEventRepository votingEventRepository, UserAppService userAppService, ServerProperties serverProperties) {
        this.votingEventRepository = votingEventRepository;
        this.userAppService = userAppService;
        this.serverProperties = serverProperties;
    }
    
    private VotingEventOut makeVotingEventOut(VotingEvent votingEvent)
    {
        return new VotingEventOut(
                votingEvent.getId(),
                votingEvent.getTitle(),
                votingEvent.getDescription(),
                votingEvent.getStartTime(),
                votingEvent.getEndTime(),
                votingEvent.getCreator().getId(),
                votingEvent.getCreator().getName()
        );
    }
    private List<VotingEventOut> listVotingEventOut(List<VotingEvent> votingEvents)
    {
        return votingEvents.stream()
                .map(votingEvent -> new VotingEventOut(
                        votingEvent.getId(),
                        votingEvent.getTitle(),
                        votingEvent.getDescription(),
                        votingEvent.getStartTime(),
                        votingEvent.getEndTime(),
                        votingEvent.getCreator().getId(),
                        votingEvent.getCreator().getName()
                ))
                .toList();
    }
    
    public List<VotingEventOut> getAllVotingEvents()
    {
        return listVotingEventOut(this.votingEventRepository.findAll());
    }
    
    public VotingEvent getVotingEventById(String id) {
        VotingEvent event = this.votingEventRepository.findById(id).orElseThrow(
                () -> new ConflictException("VotingEvent does not exist with ID: " + id)
        );
        return event;
    }
    public VotingEventOut getVotingEventOutById(String id)
    {
        VotingEvent event = getVotingEventById(id);
        return makeVotingEventOut(event);
    }
    public List<VotingEventOut> getVotingEventsByCreator(String creatorId)
    {
        UserApp creator = userAppService.getUserById(creatorId);
        
        List<VotingEvent> votingEvents = this.votingEventRepository.findByCreator(creator);
        
        return listVotingEventOut(votingEvents);
    }
    public VotingEventOut createVotingEvent(VotingEventInput votingEventInput, String creatorId) {
        
        if (votingEventInput.startTime() != null && votingEventInput.endTime() != null &&
                votingEventInput.startTime().isAfter(votingEventInput.endTime())) {
            throw new BadRequestException("Start time cannot be after end time");
        }
        
        UserApp creator = userAppService.getUserById(creatorId);
        VotingEvent votingEvent = new VotingEvent(votingEventInput);
        
        votingEvent.setId(java.util.UUID.randomUUID().toString());
        votingEvent.setCreator(creator);
        
        return makeVotingEventOut(this.votingEventRepository.save(votingEvent));
    }
    
    public boolean isBetweenTimeRanges(VotingEvent votingEvent)
    {
        LocalDateTime now = LocalDateTime.now();
        if (votingEvent.getStartTime() != null && now.isBefore(votingEvent.getStartTime())) {
            return false;
        }
        if (votingEvent.getEndTime() != null && now.isAfter(votingEvent.getEndTime())) {
            return false;
        }
        return true;
    }
    
    
    public VotingEvent patchVotingEvent(String id, VotingEvent patch) {
        VotingEvent event = this.getVotingEventById(id);
        
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
        VotingEvent event = this.getVotingEventById(id);
        
        UserApp creator = userAppService.getUserById(creatorId);
        
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
        VotingEvent event = this.getVotingEventById(id);
        this.votingEventRepository.delete(event);
        this.log.info("Deleted voting event with id {}", id);
    }
}
