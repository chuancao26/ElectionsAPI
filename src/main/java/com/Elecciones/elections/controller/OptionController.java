package com.Elecciones.elections.controller;

import com.Elecciones.elections.dto.OptionInput;
import com.Elecciones.elections.dto.OptionOut;
import com.Elecciones.elections.security.UserPrincipal;
import com.Elecciones.elections.service.OptionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/option", produces = "application/json")
@Slf4j
@AllArgsConstructor
public class OptionController
{
    
    private final OptionService optionService;
    
    @GetMapping("/voting-event/{id}")
    public ResponseEntity<List<OptionOut>> getOptionsByVotingEvent(@PathVariable String id,
                                                                   @AuthenticationPrincipal UserPrincipal userPrincipal)
    {
        List<OptionOut> options = optionService.getOptionByVoteEvent(id, userPrincipal.getId());
        return new ResponseEntity<>(options, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OptionOut>getOption(@PathVariable("id") Long id,
                                              @AuthenticationPrincipal UserPrincipal userPrincipal) {
        OptionOut option = this.optionService.getOptionOutById(id, userPrincipal.getId());
        return new ResponseEntity<>(option, HttpStatus.OK);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<OptionOut> createOption(
            @RequestBody OptionInput option,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    )
    {
        OptionOut created = this.optionService.createOption(option, userPrincipal.getId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOption(@PathVariable Long id,
                             @AuthenticationPrincipal UserPrincipal userPrincipal)
    {
        this.optionService.deleteOption(id, userPrincipal.getId());
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<OptionOut> patchOption(
            @PathVariable Long id,
            @RequestBody OptionInput patch,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    )
    {
        OptionOut updated = this.optionService.patchOption(id, patch, userPrincipal.getId());
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
    
//    @PutMapping("/{id}/{votingEventId}")
//    public ResponseEntity<?> putOption(
//            @PathVariable Long id,
//            @PathVariable String votingEventId,
//            @RequestBody Option putOption
//    ) {
//        try {
//            Option updated = this.optionService.putOption(id, putOption, votingEventId);
//            return new ResponseEntity<>(updated, HttpStatus.OK);
//        } catch (RuntimeException e) {
//            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.NOT_FOUND);
//        }
//    }

}
