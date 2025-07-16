package com.Elecciones.elections.controller;

import com.Elecciones.elections.dto.MyVote;
import com.Elecciones.elections.dto.VoteOut;
import com.Elecciones.elections.security.UserPrincipal;
import com.Elecciones.elections.service.VoteService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vote")
public class VoteController
{
    private final VoteService voteService;
    
    public VoteController(VoteService voteService)
    {
        this.voteService = voteService;
    }
    
    @GetMapping
    public ResponseEntity<List<MyVote>> getMyVotes(@AuthenticationPrincipal UserPrincipal user)
    {
        List<MyVote> votes = voteService.getAllMyVotes(user.getId());
        return new ResponseEntity<>(votes, HttpStatus.OK);
    }
    
    @PostMapping("/{optionId}")
    public ResponseEntity<VoteOut> createVote(@PathVariable("optionId") Long optionId,
                                              @AuthenticationPrincipal UserPrincipal user)
    {
        VoteOut vote = voteService.createVote(optionId, user.getId());
        return new ResponseEntity<>(vote, HttpStatus.CREATED);
    }
    
    @GetMapping("/voting-event/{id}")
    public ResponseEntity<List<VoteOut>> getAllVotesByVotingEventId(@PathVariable("id") String id,
                                                                    @AuthenticationPrincipal UserPrincipal userPrincipal)
    {
        List<VoteOut> votes = voteService.getAllByVotingEvent(id, userPrincipal.getId());
        return new ResponseEntity<>(votes, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MyVote> getVoteById(@PathVariable("id") Long id,
                                               @AuthenticationPrincipal UserPrincipal userPrincipal)
    {
        MyVote vote = voteService.getVoteOutById(id, userPrincipal.getId());
        return new ResponseEntity<>(vote, HttpStatus.OK);
    }
    
}

