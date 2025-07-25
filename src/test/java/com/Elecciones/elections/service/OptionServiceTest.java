package com.Elecciones.elections.service;

import com.Elecciones.elections.Exception.ConflictException;
import com.Elecciones.elections.Exception.ForbiddenException;
import com.Elecciones.elections.Exception.ResourceNotFoundException;
import com.Elecciones.elections.domain.Option;
import com.Elecciones.elections.domain.VotingEvent;
import com.Elecciones.elections.dto.OptionInput;
import com.Elecciones.elections.repository.OptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OptionServiceTest {
    
    @Mock
    private OptionRepository optionRepository;
    @Mock
    private ParticipantService participantService;
    @Mock
    private VotingEventService votingEventService;
    
    @InjectMocks
    private OptionService optionService;
    
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void getOptionById_shouldReturnOption_whenExists() {
        Option option = new Option(new OptionInput("event123", "Option A"));
        option.setId(1L);
        
        when(optionRepository.findById(1L)).thenReturn(Optional.of(option));
        
        Option result = optionService.getOptionById(1L);
        
        assertEquals("Option A", result.getLabel());
        verify(optionRepository, times(1)).findById(1L);
    }
    
    @Test
    void getOptionById_shouldThrow_whenNotFound() {
        when(optionRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> optionService.getOptionById(1L));
    }
    
    @Test
    void getOptionOutById_shouldReturnOut_whenAuthorized() {
        VotingEvent event = new VotingEvent();
        event.setId("event1");
        event.setTitle("Elecciones 2025");
        
        Option option = new Option(new OptionInput("event1", "Label"));
        option.setId(2L);
        option.setVotingEvent(event);
        
        when(optionRepository.findById(2L)).thenReturn(Optional.of(option));
        when(votingEventService.isVotingEventCreator("event1", "user123")).thenReturn(true);
        
        var result = optionService.getOptionOutById(2L, "user123");
        
        assertEquals("Label", result.label());
        assertEquals("event1", result.votingEventId());
    }
    
    @Test
    void getOptionOutById_shouldThrow_whenNotAuthorized() {
        VotingEvent event = new VotingEvent();
        event.setId("event1");
        
        Option option = new Option(new OptionInput("event1", "Label"));
        option.setId(2L);
        option.setVotingEvent(event);
        
        when(optionRepository.findById(2L)).thenReturn(Optional.of(option));
        when(votingEventService.isVotingEventCreator("event1", "user123")).thenReturn(false);
        when(participantService.isParticipant("user123", "event1")).thenReturn(false);
        
        assertThrows(ForbiddenException.class, () -> optionService.getOptionOutById(2L, "user123"));
    }
    
    @Test
    void createOption_shouldSucceed_whenAuthorizedAndNotStarted() {
        VotingEvent event = new VotingEvent();
        event.setId("event1");
        event.setTitle("Evento");
        
        OptionInput input = new OptionInput("event1", "Nueva opción");
        
        when(votingEventService.getVotingEventById("event1")).thenReturn(event);
        when(votingEventService.isVotingEventCreator("event1", "user123")).thenReturn(true);
        when(votingEventService.isBetweenTimeRanges(event)).thenReturn(false);
        
        ArgumentCaptor<Option> captor = ArgumentCaptor.forClass(Option.class);
        Option savedOption = new Option(input);
        savedOption.setId(5L);
        savedOption.setVotingEvent(event);
        
        when(optionRepository.save(any(Option.class))).thenReturn(savedOption);
        
        var result = optionService.createOption(input, "user123");
        
        assertEquals("Nueva opción", result.label());
        assertEquals("event1", result.votingEventId());
        verify(optionRepository).save(captor.capture());
        assertEquals("Nueva opción", captor.getValue().getLabel());
    }
    
    @Test
    void createOption_shouldThrow_whenAlreadyStarted() {
        VotingEvent event = new VotingEvent();
        event.setId("event1");
        
        OptionInput input = new OptionInput("event1", "Opción");
        
        when(votingEventService.getVotingEventById("event1")).thenReturn(event);
        when(votingEventService.isVotingEventCreator("event1", "user123")).thenReturn(true);
        when(votingEventService.isBetweenTimeRanges(event)).thenReturn(true);
        
        assertThrows(ConflictException.class, () -> optionService.createOption(input, "user123"));
    }
    
    @Test
    void patchOption_shouldUpdateLabel() {
        VotingEvent event = new VotingEvent();
        event.setId("event1");
        
        Option existing = new Option(new OptionInput("event1", "Antiguo"));
        existing.setId(1L);
        existing.setVotingEvent(event);
        
        OptionInput patch = new OptionInput("event1", "Nuevo");
        
        when(optionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(votingEventService.isVotingEventCreator("event1", "user123")).thenReturn(true);
        when(optionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        
        var result = optionService.patchOption(1L, patch, "user123");
        
        assertEquals("Nuevo", result.label());
        verify(optionRepository).save(existing);
    }
    
    @Test
    void deleteOption_shouldSucceed_whenAuthorized() {
        VotingEvent event = new VotingEvent();
        event.setId("event1");
        
        Option option = new Option(new OptionInput("event1", "Label"));
        option.setId(3L);
        option.setVotingEvent(event);
        
        when(optionRepository.findById(3L)).thenReturn(Optional.of(option));
        when(votingEventService.isVotingEventCreator("event1", "user123")).thenReturn(true);
        
        optionService.deleteOption(3L, "user123");
        
        verify(optionRepository).delete(option);
    }
    
    @Test
    void deleteOption_shouldThrow_whenNotAuthorized() {
        VotingEvent event = new VotingEvent();
        event.setId("event1");
        
        Option option = new Option(new OptionInput("event1", "Label"));
        option.setId(3L);
        option.setVotingEvent(event);
        
        when(optionRepository.findById(3L)).thenReturn(Optional.of(option));
        when(votingEventService.isVotingEventCreator("event1", "user123")).thenReturn(false);
        
        assertThrows(ForbiddenException.class, () -> optionService.deleteOption(3L, "user123"));
    }
}
