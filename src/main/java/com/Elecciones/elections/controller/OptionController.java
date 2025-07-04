package com.Elecciones.elections.controller;

import com.Elecciones.elections.domain.Option;
import com.Elecciones.elections.dto.OptionInput;
import com.Elecciones.elections.service.OptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/option", produces = "application/json")
@Slf4j
public class OptionController
{
    
    private final OptionService optionService;
    
    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }
    
    @GetMapping("/voting-event/{id}")
    public ResponseEntity<?> getOptionsByVotingEvent(@PathVariable String id)
    {
        List<Option> options = optionService.getOptionByVoteEvent(id);
        return new ResponseEntity<>(options, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getOption(@PathVariable("id") Long id) {
        try {
            Option option = this.optionService.getOptionById(id);
            return new ResponseEntity<>(option, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error getting option", e);
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.CONFLICT);
        }
    }
    
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Option> getOptions() {
        return this.optionService.getAllOptions();
    }
    
    @PostMapping(path = "/{votingEventId}", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createOption(
            @PathVariable String votingEventId,
            @RequestBody OptionInput option
    )
    {
        Option created = this.optionService.createOption(option, votingEventId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<?> patchOption(
            @PathVariable Long id,
            @RequestBody Option patch
    ) {
        try {
            Option updated = this.optionService.patchOption(id, patch);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
    
    @PutMapping("/{id}/{votingEventId}")
    public ResponseEntity<?> putOption(
            @PathVariable Long id,
            @PathVariable String votingEventId,
            @RequestBody Option putOption
    ) {
        try {
            Option updated = this.optionService.putOption(id, putOption, votingEventId);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOption(@PathVariable Long id) {
        try {
            this.optionService.deleteOption(id);
        } catch (RuntimeException e) {
            log.error("Error deleting option", e);
        }
    }
}
