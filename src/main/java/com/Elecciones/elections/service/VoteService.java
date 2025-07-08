package com.Elecciones.elections.service;

import com.Elecciones.elections.Exception.BadRequestException;
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
    
    public List<VoteOut> getAll()
    {
        List<Vote> votes = voteRepository.findAll();
        return listVoteOut(votes);
    }
    
    private Vote getVoteById(Long id)
    {
        return voteRepository.findById(id).orElseThrow(
                () -> new BadRequestException("Vote not found")
        );
    }
    public VoteOut getVoteOutById(Long id)
    {
        Vote vote = getVoteById(id);
        return makeVoteOut(vote);
    }
    public List<VoteOut> getAllByVotingEvent(String eventId)
    {
        VotingEvent votingEvent = votingEventService.getVotingEventById(eventId);
        List<Vote> votes = voteRepository.findByOption_VotingEvent(votingEvent);
        return listVoteOut(votes);
    }
    @Transactional
    public VoteOut createVote(VoteInput voteInput)
    {
        Option option = optionService.getOptionById(voteInput.optionId());
        UserApp userApp = userAppService.getUserById(voteInput.userId());
        
        Participant participant = participantService.validParticipantAndVote(userApp, option.getVotingEvent());
        participantService.markAsVoted(participant);
        
        Vote vote = new Vote();
        vote.setVotedAt(LocalDateTime.now());
        vote.setOption(option);
        vote.setVoter(userApp);
        
        return makeVoteOut(this.voteRepository.save(vote));
    }
    
}