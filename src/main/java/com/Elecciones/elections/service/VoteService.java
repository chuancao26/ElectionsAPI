package com.Elecciones.elections.service;

import com.Elecciones.elections.Exception.BadRequestException;
import com.Elecciones.elections.Exception.ConflictException;
import com.Elecciones.elections.Exception.ForbiddenException;
import com.Elecciones.elections.domain.*;
import com.Elecciones.elections.dto.*;
import com.Elecciones.elections.repository.VoteRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VoteService
{
    private final VoteRepository voteRepository;
    private final UserAppService userAppService;
    private final VotingEventService votingEventService;
    private final ParticipantService participantService;
    private final OptionService optionService;
    
    public VoteService(VoteRepository voteRepository, UserAppService userAppService, VotingEventService votingEventService, VotingEventService votingEventService1, ParticipantService participantService, OptionService optionService) {
        this.voteRepository = voteRepository;
        this.userAppService = userAppService;
        this.votingEventService = votingEventService1;
        this.participantService = participantService;
        this.optionService = optionService;
    }
    
    private VoteOut makeVoteOut(Vote vote)
    {
        return new VoteOut(
                vote.getId(),
                vote.getVoter().getId(),
                vote.getVoter().getName(),
                vote.getOption().getId(),
                vote.getOption().getLabel()
        );
    }
    
    private List<VoteOut> listVoteOut(List<Vote> votes)
    {
        return votes.stream()
                .map(
                        vote -> new VoteOut(
                                vote.getId(),
                                vote.getVoter().getId(),
                                vote.getVoter().getName(),
                                vote.getOption().getId(),
                                vote.getOption().getLabel()
                        )
                )
                .toList();
    }
    
    private void validateCreator(String eventId, String creatorId)
    {
        VotingEvent votingEvent = this.votingEventService.getVotingEventById(eventId);
        if (!votingEvent.getCreator().getId().equals(creatorId))
        {
            throw new ForbiddenException("You do not have permission to access.");
        }
    }
    public List<MyVote> getAllMyVotes(String voterId)
    {
        List<Vote> votes = voteRepository.findAll().stream().filter(
                v -> v.getVoter().getId().equals(voterId))
                .toList();
        return votes.stream().map(
                v -> new MyVote(v.getOption().getVotingEvent().getTitle(),
                        v.getOption().getLabel(),
                        v.getVotedAt()))
                .toList();
    }
    
    private Vote getVoteById(Long id)
    {
        return voteRepository.findById(id).orElseThrow(
                () -> new BadRequestException("Vote not found")
        );
    }
    public MyVote getVoteOutById(Long id, String voterId)
    {
        Vote vote = getVoteById(id);
        if (!vote.getVoter().getId().equals(voterId))
        {
            throw new ForbiddenException("You do not have permission to access.");
        }
        return new MyVote(vote.getOption().getVotingEvent().getTitle(),
                vote.getOption().getLabel(), vote.getVotedAt());
    }
    public List<VoteOut> getAllByVotingEvent(String eventId, String creatorId)
    {
        validateCreator(eventId, creatorId);
        
        VotingEvent votingEvent = votingEventService.getVotingEventById(eventId);
        List<Vote> votes = voteRepository.findByOption_VotingEvent(votingEvent);
        
        return listVoteOut(votes);
    }
    @Transactional
    public VoteOut createVote(Long optionId, String creatorId)
    {
        Option option = optionService.getOptionById(optionId);
        UserApp userApp = userAppService.getUserById(creatorId);
        
        VotingEvent votingEvent = option.getVotingEvent();
        
        if (!votingEventService.isBetweenTimeRanges(votingEvent))
        {
            throw new ConflictException("The voting event time range is invalid");
        }
        
        Participant participant = participantService.validParticipantAndVote(userApp, votingEvent);
        participantService.markAsVoted(participant);
        
        Vote vote = new Vote();
        vote.setVotedAt(LocalDateTime.now());
        vote.setOption(option);
        vote.setVoter(userApp);
        
        return makeVoteOut(this.voteRepository.save(vote));
    }
    
}