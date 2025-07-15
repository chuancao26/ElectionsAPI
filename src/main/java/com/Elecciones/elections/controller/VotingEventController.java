package com.Elecciones.elections.controller;

import com.Elecciones.elections.dto.VotingEventInput;
import com.Elecciones.elections.dto.VotingEventOut;
import com.Elecciones.elections.security.UserPrincipal;
import com.Elecciones.elections.service.VotingEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<VotingEventOut> getVotingEvent(@PathVariable("id") String id,
                                                         @AuthenticationPrincipal UserPrincipal user)
    {
        VotingEventOut event = this.votingEventService.getVotingEventOutById(id, user.getId());
        return new ResponseEntity<>(event, HttpStatus.OK);
    }
    
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<VotingEventOut> getVotingEvents(@AuthenticationPrincipal UserPrincipal user) {
        return this.votingEventService.getAllVotingEvents(user.getId());
    }
    
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<VotingEventOut> createVotingEvent(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody VotingEventInput votingEventInput)
    {
        VotingEventOut created = this.votingEventService.createVotingEvent(votingEventInput,
                user.getId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<VotingEventOut> patchVotingEvent(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody VotingEventInput patch
    ) {
        VotingEventOut updated = this.votingEventService.patchVotingEvent(id, user.getId(), patch);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVotingEvent(@PathVariable String id,
                                  @AuthenticationPrincipal UserPrincipal user)
    {
        this.votingEventService.deleteVotingEvent(id, user.getId());
    }
//    @PutMapping("/{id}/{creatorId}")
//    public ResponseEntity<VotingEventOut> putVotingEvent(
//            @PathVariable String id,
//            @PathVariable String creatorId,
//            @RequestBody VotingEventInput putVotingEvent
//    )
//    {
//        VotingEventOut updated = this.votingEventService.putVotingEvent(id, putVotingEvent, creatorId);
//        return new ResponseEntity<>(updated, HttpStatus.OK);
//    }
    
}
