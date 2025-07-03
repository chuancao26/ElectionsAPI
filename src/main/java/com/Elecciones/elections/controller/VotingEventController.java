package com.Elecciones.elections.controller;

import com.Elecciones.elections.domain.VotingEvent;
import com.Elecciones.elections.service.VotingEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "/api/voting-event", produces = "application/json")
@Slf4j
public class VotingEventController {
    
    private final VotingEventService votingEventService;
    
    public VotingEventController(VotingEventService votingEventService) {
        this.votingEventService = votingEventService;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getVotingEvent(@PathVariable("id") String id) {
        try {
            VotingEvent event = this.votingEventService.getVotingEventById(id);
            return new ResponseEntity<>(event, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error getting voting event", e);
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.CONFLICT);
        }
    }
    
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Iterable<VotingEvent> getVotingEvents() {
        return this.votingEventService.getAllVotingEvents();
    }
    
    @PostMapping(path = "/{creatorId}", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createVotingEvent(
            @PathVariable String creatorId,
            @RequestBody VotingEvent votingEvent
    ) {
        try {
            VotingEvent created = this.votingEventService.createVotingEvent(votingEvent, creatorId);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            log.error("Error creating voting event", e);
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.CONFLICT);
        }
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<?> patchVotingEvent(
            @PathVariable String id,
            @RequestBody VotingEvent patch
    ) {
        try {
            VotingEvent updated = this.votingEventService.patchVotingEvent(id, patch);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
    
    @PutMapping("/{id}/{creatorId}")
    public ResponseEntity<?> putVotingEvent(
            @PathVariable String id,
            @PathVariable String creatorId,
            @RequestBody VotingEvent putVotingEvent
    ) {
        try {
            VotingEvent updated = this.votingEventService.putVotingEvent(id, putVotingEvent, creatorId);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVotingEvent(@PathVariable String id) {
        try {
            this.votingEventService.deleteVotingEvent(id);
        } catch (RuntimeException e) {
            log.error("Error deleting voting event", e);
        }
    }
}
