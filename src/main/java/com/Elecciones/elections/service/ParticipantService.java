package com.Elecciones.elections.service;

import com.Elecciones.elections.Exception.BadRequestException;
import com.Elecciones.elections.domain.Participant;
import com.Elecciones.elections.domain.Status;
import com.Elecciones.elections.domain.UserApp;
import com.Elecciones.elections.domain.VotingEvent;
import com.Elecciones.elections.dto.ParticipantInput;
import com.Elecciones.elections.dto.ParticipantOut;
import com.Elecciones.elections.repository.ParticipantRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParticipantService
{
    private final ParticipantRepository participantRepository;
    private final UserAppService userAppService;
    private final VotingEventService votingEventService;
    
    public ParticipantService(ParticipantRepository participantRepository, UserAppService userAppService, VotingEventService votingEventService)
    {
        this.participantRepository = participantRepository;
        this.userAppService = userAppService;
        this.votingEventService = votingEventService;
    }
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
        Participant participant = participantRepository.findById(id).orElseThrow(
                () -> new BadRequestException("There is no participant with id: " + id)
        );
        return participant;
    }
    public ParticipantOut getParticipantOutById(Long id)
    {
        Participant participant = getParticipantById(id);
        return makeParticipantOut(participant);
    }
    
    public List<ParticipantOut> getParticipantsByEventId(String eventId)
    {
        VotingEvent votingEvent = votingEventService.getVotingEventById(eventId);
        List<Participant> participants = participantRepository.findByVotingEvent(votingEvent);
        return listParticipantOut(participants);
    }
    
    public Optional<Participant> getParticipantByIdAndVotingEvent(String votingEventId, String userId)
    {
        VotingEvent votingEvent = votingEventService.getVotingEventById(votingEventId);
        UserApp userApp = userAppService.getUserById(userId);
        return participantRepository.findByUserAndVotingEvent(userApp, votingEvent);
    }
    
    public ParticipantOut createParticipant(ParticipantInput participant)
    {
        
        Optional<Participant> existedParticipant = getParticipantByIdAndVotingEvent(participant.eventId(), participant.userId());
        if (existedParticipant.isPresent())
        {
            return makeParticipantOut(existedParticipant.get());
        }
        
        UserApp userApp = userAppService.getUserById(participant.userId());
        VotingEvent votingEvent = votingEventService.getVotingEventById(participant.eventId());
        
        Participant newParticipant = new Participant();
        newParticipant.setVotingEvent(votingEvent);
        newParticipant.setUser(userApp);
        newParticipant.setStatus(Status.PENDING);
        
        participantRepository.save(newParticipant);
        return makeParticipantOut(newParticipant);
    }
    
    public ParticipantOut setbanParticipant(Long id)
    {
        Participant currentParticipant = this.getParticipantById(id);
        currentParticipant.setStatus(Status.BANNED);
        participantRepository.save(currentParticipant);
        return makeParticipantOut(currentParticipant);
    }
    
    public ParticipantOut setVotedParticipant(Long id)
    {
        Participant currentParticipant = this.getParticipantById(id);
        currentParticipant.setStatus(Status.VOTED);
        participantRepository.save(currentParticipant);
        return makeParticipantOut(currentParticipant);
    }
    
    public ParticipantOut setPendingParticipant(Long id)
    {
        Participant currentParticipant = this.getParticipantById(id);
        currentParticipant.setStatus(Status.PENDING);
        participantRepository.save(currentParticipant);
        return makeParticipantOut(currentParticipant);
    }
    
}
