package com.Elecciones.elections.controller;

import com.Elecciones.elections.domain.VotingEvent;
import com.Elecciones.elections.dto.VotingEventInput;
import com.Elecciones.elections.dto.VotingEventOut;
import com.Elecciones.elections.service.VotingEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/voting-event", produces = "application/json")
@Slf4j
public class VotingEventController {
    
    private final VotingEventService votingEventService;
    
    public VotingEventController(VotingEventService votingEventService) {
        this.votingEventService = votingEventService;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<VotingEventOut> getVotingEvent(@PathVariable("id") String id)
    {
        VotingEventOut event = this.votingEventService.getVotingEventOutById(id);
        return new ResponseEntity<>(event, HttpStatus.OK);
    }
    
    @GetMapping ("/creator/{creatorId}")
    public ResponseEntity<List<VotingEventOut>> getVotingEventByCreator(@PathVariable("creatorId") String creatorId)
    {
        List<VotingEventOut> events = this.votingEventService.getVotingEventsByCreator(creatorId);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }
    
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<VotingEventOut> getVotingEvents() {
        return this.votingEventService.getAllVotingEvents();
    }
    
    @PostMapping(path = "/{creatorId}", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<VotingEventOut> createVotingEvent(
            @PathVariable String creatorId,
            @RequestBody VotingEventInput votingEventInput)
    {
        VotingEventOut created = this.votingEventService.createVotingEvent(votingEventInput, creatorId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<VotingEventOut> patchVotingEvent(
            @PathVariable String id,
            @RequestBody VotingEventInput patch
    ) {
        VotingEventOut updated = this.votingEventService.patchVotingEvent(id, patch);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
    
    @PutMapping("/{id}/{creatorId}")
    public ResponseEntity<VotingEventOut> putVotingEvent(
            @PathVariable String id,
            @PathVariable String creatorId,
            @RequestBody VotingEventInput putVotingEvent
    )
    {
        VotingEventOut updated = this.votingEventService.putVotingEvent(id, putVotingEvent, creatorId);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVotingEvent(@PathVariable String id)
    {
        this.votingEventService.deleteVotingEvent(id);
    }
}
