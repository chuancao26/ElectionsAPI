package com.Elecciones.elections.service;

import com.Elecciones.elections.Exception.BadRequestException;
import com.Elecciones.elections.domain.Participant;
import com.Elecciones.elections.domain.Status;
import com.Elecciones.elections.domain.UserApp;
import com.Elecciones.elections.domain.VotingEvent;
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
    public Participant getParticipantById(Long id)
    {
        Participant participant = participantRepository.findById(id).orElseThrow(
                () -> new BadRequestException("There is no participant with id: " + id)
        );
        return participant;
    }
    
    public List<Participant> getParticipantsByEventId(String eventId)
    {
        VotingEvent votingEvent = votingEventService.getVotingEventById(eventId);
        List<Participant> participants = participantRepository.findByVotingEvent(votingEvent);
        return participants;
    }
    
    public Optional<Participant> getExistedParticipantByIdAndVotingEvent(String votingEventId, String userId)
    {
        return participantRepository.findByUserIdAndVotingEventId(userId, votingEventId);
    }
    
    public Participant createParticipant(String votingEventId, String userId)
    {
        Optional<Participant> existedParticipant = getExistedParticipantByIdAndVotingEvent(votingEventId, userId);
        if (existedParticipant.isPresent())
        {
            return existedParticipant.get();
        }
        
        UserApp userApp = userAppService.getUserById(userId);
        VotingEvent votingEvent = votingEventService.getVotingEventById(votingEventId);
        
        Participant participant = new Participant();
        participant.setVotingEvent(votingEvent);
        participant.setParticipant(userApp);
        participant.setStatus(Status.VALID);
        
        participantRepository.save(participant);
        return participant;
    }
    
    public Participant banParticipant(Participant participant)
    {
        Participant currentParticipant = this.getParticipantById(participant.getId());
        currentParticipant.setStatus(Status.BANNED);
        participantRepository.save(currentParticipant);
        return currentParticipant;
    }
}
