package com.Elecciones.elections.controller;

import com.Elecciones.elections.domain.Participant;
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
    @GetMapping("/voting-event/{id}")
    public ResponseEntity<?> getParticipantsByVotingEventId(@PathVariable("id") String id)
    {
        List<ParticipantOut> participants = participantService.getParticipantsByEventId(id);
        return new ResponseEntity<>(participants, HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<?> createParticipant(@RequestBody ParticipantInput participant)
    {
        ParticipantOut newParticipant = participantService.createParticipant(participant);
        return new ResponseEntity<>(newParticipant, HttpStatus.CREATED);
    }
    
    @PostMapping("/ban")
    public ResponseEntity<?> banParticipant(@RequestBody Long id)
    {
        ParticipantOut participantOut = participantService.setbanParticipant(id);
        return new ResponseEntity<>(participantOut, HttpStatus.OK);
    }
    
//    @PostMapping("/pending") public ResponseEntity<?> validParticipant(@RequestBody Long id)
//    {
//        ParticipantOut participantOut = participantService.validParticipant(id);
//        return new ResponseEntity<>(participantOut, HttpStatus.OK);
//    }
    
}
