package com.Elecciones.elections.controller;

import com.Elecciones.elections.domain.Option;
import com.Elecciones.elections.dto.OptionInput;
import com.Elecciones.elections.dto.OptionOut;
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
    public ResponseEntity<List<OptionOut>> getOptionsByVotingEvent(@PathVariable String id)
    {
        List<OptionOut> options = optionService.getOptionByVoteEvent(id);
        return new ResponseEntity<>(options, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OptionOut>getOption(@PathVariable("id") Long id) {
        OptionOut option = this.optionService.getOptionOutById(id);
        return new ResponseEntity<>(option, HttpStatus.OK);
    }
    
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<OptionOut>> getOptions()
    {
        return ResponseEntity.ok(this.optionService.getAllOptions());
    }
    
    @PostMapping(path = "/{votingEventId}", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<OptionOut> createOption(
            @PathVariable String votingEventId,
            @RequestBody OptionInput option
    )
    {
        OptionOut created = this.optionService.createOption(option, votingEventId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOption(@PathVariable Long id) {
        this.optionService.deleteOption(id);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<Option> patchOption(
            @PathVariable Long id,
            @RequestBody Option patch
    )
    {
        Option updated = this.optionService.patchOption(id, patch);
        return new ResponseEntity<>(updated, HttpStatus.OK);
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
    
}
