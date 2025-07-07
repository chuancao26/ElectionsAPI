package com.Elecciones.elections.controller;

import com.Elecciones.elections.dto.VoteInput;
import com.Elecciones.elections.dto.VoteOut;
import com.Elecciones.elections.service.VoteService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> getAllVotes()
    {
        List<VoteOut> votes = voteService.getAll();
        return new ResponseEntity<>(votes, HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<?> createVote(@RequestBody VoteInput voteInput)
    {
        VoteOut vote = voteService.createVote(voteInput);
        return new ResponseEntity<>(vote, HttpStatus.CREATED);
    }
    
    @GetMapping("/voting-event/{id}")
    public ResponseEntity<?> getAllVotesByVotingEventId(@PathVariable("id") String id)
    {
        List<VoteOut> votes = voteService.getAllByVotingEvent(id);
        return new ResponseEntity<>(votes, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getVoteById(@PathVariable("id") Long id)
    {
        VoteOut vote = voteService.getVoteOutById(id);
        return new ResponseEntity<>(vote, HttpStatus.OK);
    }
    
}

