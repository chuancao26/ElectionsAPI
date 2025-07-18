package com.Elecciones.elections.service;

import com.Elecciones.elections.Exception.ConflictException;
import com.Elecciones.elections.Exception.ForbiddenException;
import com.Elecciones.elections.Exception.ResourceNotFoundException;
import com.Elecciones.elections.domain.*;
import com.Elecciones.elections.dto.ParticipantOut;
import com.Elecciones.elections.repository.ParticipantRepository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ParticipantService
{
    private final ParticipantRepository participantRepository;
    private final UserAppService userAppService;
    private final VotingEventService votingEventService;
    
    private ParticipantOut makeParticipantOut(Participant participant)
    {
        return new ParticipantOut(
                participant.getId(),
                participant.getStatus(),
                participant.getUser().getId(),
                participant.getUser().getName(),
                participant.getVotingEvent().getId(),
                participant.getVotingEvent().getTitle()
        );
    }
    private List<ParticipantOut> listParticipantOut(List<Participant> participants)
    {
        return participants.stream()
                .map(
                        participant -> new ParticipantOut(
                                participant.getId(),
                                participant.getStatus(),
                                participant.getUser().getId(),
                                participant.getUser().getName(),
                                participant.getVotingEvent().getId(),
                                participant.getVotingEvent().getTitle()
                        ))
                .toList();
    }
    
    private Participant getParticipantById(Long id)
    {
        return participantRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("There is no participant with id: " + id)
        );
    }
    public ParticipantOut getParticipantOutById(Long id, String userID)
    {
        Participant participant = getParticipantById(id);
        if (!votingEventService.isVotingEventCreator(participant.getVotingEvent().getId(), userID))
        {
            if (!participant.getUser().getId().equals(userID))
            {
                throw new ForbiddenException("You are not allowed to perform this operation");
            }
        }
        return makeParticipantOut(participant);
    }
    
    public List<ParticipantOut> getParticipantsOutByEventId(String eventId, String creatorId)
    {
        if (!votingEventService.isVotingEventCreator(eventId, creatorId))
        {
            throw new ForbiddenException("You are not allowed to perform this operation");
        }
        VotingEvent votingEvent = votingEventService.getVotingEventById(eventId);
        
        List<Participant> participants = participantRepository.findByVotingEvent(votingEvent);
        
        return listParticipantOut(participants);
    }
    
    // inscribirse
    public ParticipantOut createParticipant(String eventId, String userId)
    {
        if (votingEventService.isVotingEventCreator(eventId, userId))
        {
            throw new ForbiddenException("You are not allowed to perform this operation");
        }
        
        VotingEvent votingEvent = votingEventService.getVotingEventById(eventId);
        if (votingEvent.getStatus() == VotingEventStatus.CLOSED)
        {
            throw new ConflictException("Voting Event is closed");
        }
        UserApp userApp = userAppService.getUserById(userId);
        
        Optional<Participant> existedParticipant = participantRepository.findByUserAndVotingEvent(userApp, votingEvent);
        
        if (existedParticipant.isPresent())
        {
            return makeParticipantOut(existedParticipant.get());
        }
        
        Participant newParticipant = new Participant();
        newParticipant.setVotingEvent(votingEvent);
        newParticipant.setUser(userApp);
        newParticipant.setStatus(Status.PENDING);
        
        return makeParticipantOut(participantRepository.save(newParticipant));
    }
    
    public ParticipantOut setBanParticipant(Long id, String creatorId)
    {
        Participant currentParticipant = this.getParticipantById(id);
        if (!votingEventService.isVotingEventCreator(currentParticipant.getVotingEvent().getId(), creatorId))
        {
            throw new ForbiddenException("You are not allowed to perform this operation");
        }
        currentParticipant.setStatus(Status.BANNED);
        
        return makeParticipantOut(participantRepository.save(currentParticipant));
    }
    
//    public ParticipantOut setVotedParticipant(Long id)
//    {
//        Participant currentParticipant = this.getParticipantById(id);
//        currentParticipant.setStatus(Status.VOTED);
//        participantRepository.save(currentParticipant);
//        return makeParticipantOut(currentParticipant);
//    }
//
//    public ParticipantOut setPendingParticipant(Long id)
//    {
//        Participant currentParticipant = this.getParticipantById(id);
//        currentParticipant.setStatus(Status.PENDING);
//        participantRepository.save(currentParticipant);
//        return makeParticipantOut(currentParticipant);
//    }
    
    public Participant validParticipantAndVote(UserApp userApp, VotingEvent votingEvent)
    {
        Participant participant = participantRepository.findByUserAndVotingEvent(userApp, votingEvent).orElseThrow(
                () -> new ResourceNotFoundException("There is no participant with id: " + votingEvent.getId())
        );
        // is banned?
        if (participant.getStatus() == Status.BANNED)
        {
            throw new ConflictException("Participant is banned");
        }
        // has already voted?
        if (participant.getStatus() == Status.VOTED)
        {
            throw new ConflictException("Participant has already voted");
        }
        return participant;
    }
    public void markAsVoted(Participant participant)
    {
        participant.setStatus(Status.VOTED);
        participantRepository.save(participant);
    }
    public boolean isParticipant(String userId, String eventId)
    {
        VotingEvent votingEvent = votingEventService.getVotingEventById(eventId);
        return this.participantRepository.findByVotingEvent(votingEvent).stream().anyMatch(
                v -> v.getUser().getId().equals(userId));
    }
}
