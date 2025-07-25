package com.Elecciones.elections.service;

import com.Elecciones.elections.Exception.BadRequestException;
import com.Elecciones.elections.Exception.ConflictException;
import com.Elecciones.elections.Exception.ForbiddenException;
import com.Elecciones.elections.domain.UserApp;
import com.Elecciones.elections.domain.VotingEvent;
import com.Elecciones.elections.domain.VotingEventStatus;
import com.Elecciones.elections.dto.VotingEventInput;
import com.Elecciones.elections.dto.VotingEventOut;
import com.Elecciones.elections.repository.VotingEventRepository;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class VotingEventService {
    private final VotingEventRepository votingEventRepository;
    private final UserAppService userAppService;
    
    private VotingEventOut makeVotingEventOut(VotingEvent votingEvent) {
        return new VotingEventOut(
                votingEvent.getId(),
                votingEvent.getTitle(),
                votingEvent.getDescription(),
                votingEvent.getStartTime(),
                votingEvent.getEndTime(),
                votingEvent.getCreator().getId(),
                votingEvent.getCreator().getName(),
                votingEvent.getStatus()
        );
    }
    
    private List<VotingEventOut> listVotingEventOut(List<VotingEvent> votingEvents) {
        return votingEvents.stream()
                .map(votingEvent -> new VotingEventOut(
                        votingEvent.getId(),
                        votingEvent.getTitle(),
                        votingEvent.getDescription(),
                        votingEvent.getStartTime(),
                        votingEvent.getEndTime(),
                        votingEvent.getCreator().getId(),
                        votingEvent.getCreator().getName(),
                        votingEvent.getStatus()
                ))
                .toList();
    }
    
    public List<VotingEventOut> getAllVotingEvents(String userId) {
        return listVotingEventOut(this.votingEventRepository.findAll().stream()
                .filter(v -> v.getCreator().getId().equals(userId))
                .toList());
    }
    
    public VotingEvent getVotingEventById(String id)
    {
        return this.votingEventRepository.findById(id).orElseThrow(
                () -> new ConflictException("VotingEvent does not exist with ID: " + id)
        );
    }
    
    public VotingEventOut getVotingEventOutById(String eventId, String userId)
    {
        VotingEvent event = getVotingEventById(eventId);
        if (!event.getCreator().getId().equals(userId))
        {
            throw new ForbiddenException("You are not allowed to perform this action");
        }
        return makeVotingEventOut(event);
    }
    
    public VotingEventOut createVotingEvent(VotingEventInput votingEventInput, String creatorId)
    {
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
    
    public boolean isBetweenTimeRanges(VotingEvent votingEvent) {
        LocalDateTime now = LocalDateTime.now();
        if (votingEvent.getStartTime() != null && now.isBefore(votingEvent.getStartTime())) {
            return false;
        }
        if (votingEvent.getEndTime() != null && now.isAfter(votingEvent.getEndTime())) {
            return false;
        }
        return true;
    }
    
    public VotingEventOut patchVotingEvent(String id,
                                           String userId,
                                           VotingEventInput patch)
    {
        VotingEvent event = this.getVotingEventById(id);
        if (!event.getCreator().getId().equals(userId))
        {
            throw new ForbiddenException("You are not allowed to perform this action");
        }
        if (patch.title() != null) {
            event.setTitle(patch.title());
        }
        if (patch.description() != null) {
            event.setDescription(patch.description());
        }
        if (patch.startTime() != null) {
            event.setStartTime(patch.startTime());
        }
        if (patch.endTime() != null) {
            event.setEndTime(patch.endTime());
        }
        
        if (event.getStartTime() != null && event.getEndTime() != null &&
                event.getStartTime().isAfter(event.getEndTime())) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        }
        
        return makeVotingEventOut(this.votingEventRepository.save(event));
    }
    
    public void deleteVotingEvent(String id, String userId)
    {
        VotingEvent event = this.getVotingEventById(id);
        if (!event.getCreator().getId().equals(userId))
        {
            throw new ForbiddenException("You are not allowed to perform this action");
        }
        this.votingEventRepository.delete(event);
    }
    public boolean isVotingEventCreator(String eventId, String creatorId)
    {
        VotingEvent event = this.getVotingEventById(eventId);
        return event.getCreator().getId().equals(creatorId);
    }
    
    public void closeVotingEvent(String eventId, String creatorId)
    {
        if (!isVotingEventCreator(eventId, creatorId))
        {
            throw new ForbiddenException("You are not allowed to perform this action");
        }
        VotingEvent votingEvent = this.getVotingEventById(eventId);
        votingEvent.setStatus(VotingEventStatus.CLOSED);
        votingEventRepository.save(votingEvent);
    }
    public void openVotingEvent(String eventId, String creatorId)
    {
        if (!isVotingEventCreator(eventId, creatorId))
        {
            throw new ForbiddenException("You are not allowed to perform this action");
        }
        
        VotingEvent votingEvent = this.getVotingEventById(eventId);
        votingEvent.setStatus(VotingEventStatus.OPENED);
        votingEventRepository.save(votingEvent);
        
    }
    
}
