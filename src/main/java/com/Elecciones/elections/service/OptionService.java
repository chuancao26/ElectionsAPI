package com.Elecciones.elections.service;

import com.Elecciones.elections.Exception.ConflictException;
import com.Elecciones.elections.Exception.ForbiddenException;
import com.Elecciones.elections.Exception.ResourceNotFoundException;
import com.Elecciones.elections.domain.Option;
import com.Elecciones.elections.domain.VotingEvent;
import com.Elecciones.elections.dto.OptionInput;
import com.Elecciones.elections.dto.OptionOut;
import com.Elecciones.elections.repository.OptionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@AllArgsConstructor
public class OptionService
{
    
    private final OptionRepository optionRepository;
    private final ParticipantService participantService;
    private final VotingEventService votingEventService;
    
    private List<OptionOut> listOptionOut(List<Option> options)
    {
        return options.stream()
                .map(
                        option -> new OptionOut(
                                option.getId(),
                                option.getLabel(),
                                option.getVotingEvent().getId(),
                                option.getVotingEvent().getTitle()
                        )
                )
                .toList();
    }
    private OptionOut makeOptionOut(Option option)
    {
        return new OptionOut(
                option.getId(),
                option.getLabel(),
                option.getVotingEvent().getId(),
                option.getVotingEvent().getTitle()
        );
    }
    public Option getOptionById(Long id) {
        return this.optionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Option does not exist with ID: " + id)
        );
    }
    public OptionOut getOptionOutById(Long id, String userId)
    {
        Option option = this.getOptionById(id);
        if (!votingEventService.isVotingEventCreator(option.getVotingEvent().getId(), userId))
        {
            if (!participantService.isParticipant(userId, option.getVotingEvent().getId()))
            {
                throw new ForbiddenException("You are not participant or creator of this event");
            }
        }
        return makeOptionOut(option);
    }
    public List<OptionOut> getOptionByVoteEvent(String eventId, String userId)
    {
        VotingEvent votingEvent = votingEventService.getVotingEventById(eventId);
        if (!votingEventService.isVotingEventCreator(eventId, userId))
        {
            if (!participantService.isParticipant(userId, votingEvent.getId()))
            {
                throw new ForbiddenException("You are not participant or creator of this event");
            }
        }
        
        List<Option> option = optionRepository.findByVotingEvent(votingEvent);
        return listOptionOut(option);
    }
    
    public OptionOut createOption(OptionInput optionInput, String creatorId)
    {
        VotingEvent votingEvent = this.votingEventService.getVotingEventById(optionInput.eventId());
        if (!votingEventService.isVotingEventCreator(votingEvent.getId(), creatorId ))
        {
            throw new ForbiddenException("You are not allowed");
        }
        
        if (votingEventService.isBetweenTimeRanges(votingEvent))
        {
            throw new ConflictException("The voting event has started");
        }
        
        Option option = new Option(optionInput);
        option.setVotingEvent(votingEvent);
        
        return makeOptionOut(this.optionRepository.save(option));
    }
    
    public void deleteOption(Long id, String creatorId)
    {
        Option option = this.getOptionById(id);
        if (!votingEventService.isVotingEventCreator(option.getVotingEvent().getId(), creatorId ))
        {
            throw new ForbiddenException("You are not allowed to perform this operation");
        }
        this.optionRepository.delete(option);
    }
    
    
    
    public OptionOut patchOption(Long id, OptionInput patch, String creatorId)
    {
        Option option = this.getOptionById(id);
        if (!votingEventService.isVotingEventCreator(option.getVotingEvent().getId(), creatorId ))
        {
            throw new ForbiddenException("You are not allowed to perform this operation");
        }
        
        if (patch.label() != null) {
            option.setLabel(patch.label());
        }
        
        option.setModifiedAt(LocalDateTime.now());
        
        return makeOptionOut(this.optionRepository.save(option));
    }
    
//    public Option putOption(Long id, Option putOption, String votingEventId) {
//        Option option = this.getOptionById(id);
//
//        VotingEvent votingEvent = this.votingEventService.getVotingEventById(votingEventId);
//
//        putOption.setId(id);
//        putOption.setCreatedAt(option.getCreatedAt());
//        putOption.setVotingEvent(votingEvent);
//
//        return this.optionRepository.save(putOption);
//    }
//    public List<OptionOut> getAllOptions() {
//        List<OptionOut> options = listOptionOut(this.optionRepository.findAll());
//        return options;
//    }
}
