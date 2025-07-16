package com.Elecciones.elections.controller;

import com.Elecciones.elections.dto.ParticipantInput;
import com.Elecciones.elections.dto.ParticipantOut;
import com.Elecciones.elections.service.ParticipantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ParticipantOut> getParticipantById(@PathVariable Long id)
    {
        ParticipantOut participantOut = participantService.getParticipantOutById(id);
        return new ResponseEntity<>(participantOut, HttpStatus.OK);
    }
    @GetMapping("/voting-event/{id}")
    public ResponseEntity<List<ParticipantOut>> getParticipantsByVotingEventId(@PathVariable("id") String id)
    {
        List<ParticipantOut> participants = participantService.getParticipantsOutByEventId(id);
        return new ResponseEntity<>(participants, HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<ParticipantOut> createParticipant(@RequestBody ParticipantInput participant)
    {
        ParticipantOut newParticipant = participantService.createParticipant(participant);
        return new ResponseEntity<>(newParticipant, HttpStatus.CREATED);
    }
    
    @PostMapping("/ban")
    public ResponseEntity<ParticipantOut> banParticipant(@RequestBody Long id)
    {
        ParticipantOut participantOut = participantService.setBanParticipant(id);
        return new ResponseEntity<>(participantOut, HttpStatus.OK);
    }
    
//    @PostMapping("/pending") public ResponseEntity<?> validParticipant(@RequestBody Long id)
//    {
//        ParticipantOut participantOut = participantService.validParticipant(id);
//        return new ResponseEntity<>(participantOut, HttpStatus.OK);
//    }
    
}
