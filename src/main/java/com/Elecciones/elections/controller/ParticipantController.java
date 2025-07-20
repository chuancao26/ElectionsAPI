package com.Elecciones.elections.controller;

import com.Elecciones.elections.dto.ParticipantOut;
import com.Elecciones.elections.security.UserPrincipal;
import com.Elecciones.elections.service.ParticipantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participant")
public class ParticipantController
{
    private ParticipantService participantService;
    
    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ParticipantOut> getParticipantById(@PathVariable Long id,
                                                             @AuthenticationPrincipal UserPrincipal userPrincipal)
    {
        ParticipantOut participantOut = participantService.getParticipantOutById(id, userPrincipal.getId());
        return new ResponseEntity<>(participantOut, HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<List<ParticipantOut>> getMyParticipations(@AuthenticationPrincipal UserPrincipal userPrincipal)
    {
        return new ResponseEntity<>(participantService.getMyParticipations(userPrincipal.getId())
                ,HttpStatus.OK);
    }
    @GetMapping("/voting-event/{id}")
    public ResponseEntity<List<ParticipantOut>> getParticipantsByVotingEventId(@PathVariable("id") String id,
                                                             @AuthenticationPrincipal UserPrincipal userPrincipal)
    {
        List<ParticipantOut> participants = participantService.getParticipantsOutByEventId(id, userPrincipal.getId());
        return new ResponseEntity<>(participants, HttpStatus.OK);
    }
    
    @PostMapping("/voting-event/{votingId}")
    public ResponseEntity<ParticipantOut> createParticipant(@PathVariable("votingId") String votingId,
                                                           @AuthenticationPrincipal UserPrincipal userPrincipal
                                                            )
    {
        ParticipantOut newParticipant = participantService.createParticipant(votingId, userPrincipal.getId());
        return new ResponseEntity<>(newParticipant, HttpStatus.CREATED);
    }
    
    @PostMapping("/ban/{id}")
    public ResponseEntity<ParticipantOut> banParticipant(@PathVariable("id") Long id,
                                                         @AuthenticationPrincipal UserPrincipal userPrincipal)
    {
        ParticipantOut participantOut = participantService.setBanParticipant(id, userPrincipal.getId());
        return new ResponseEntity<>(participantOut, HttpStatus.OK);
    }
    
//    @PostMapping("/pending") public ResponseEntity<?> validParticipant(@RequestBody Long id)
//    {
//        ParticipantOut participantOut = participantService.validParticipant(id);
//        return new ResponseEntity<>(participantOut, HttpStatus.OK);
//    }
    
}
