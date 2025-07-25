package com.Elecciones.elections.service;

import com.Elecciones.elections.Exception.BadRequestException;
import com.Elecciones.elections.Exception.ConflictException;
import com.Elecciones.elections.Exception.ForbiddenException;
import com.Elecciones.elections.domain.UserApp;
import com.Elecciones.elections.domain.VotingEvent;
import com.Elecciones.elections.domain.VotingEventStatus;
import com.Elecciones.elections.dto.VotingEventInput;
import com.Elecciones.elections.repository.VotingEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VotingEventServiceTest {
    
    @Mock
    private VotingEventRepository votingEventRepository;
    
    @Mock
    private UserAppService userAppService;
    
    @InjectMocks
    private VotingEventService votingEventService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void getVotingEventById_shouldReturnEvent_whenExists() {
        VotingEvent event = new VotingEvent();
        event.setId("event1");
        
        when(votingEventRepository.findById("event1")).thenReturn(Optional.of(event));
        
        VotingEvent result = votingEventService.getVotingEventById("event1");
        
        assertEquals("event1", result.getId());
    }
    
    @Test
    void getVotingEventById_shouldThrow_whenNotFound() {
        when(votingEventRepository.findById("event1")).thenReturn(Optional.empty());
        
        assertThrows(ConflictException.class, () -> votingEventService.getVotingEventById("event1"));
    }
    
    @Test
    void createVotingEvent_shouldSucceed() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        VotingEventInput input = new VotingEventInput("Título", "Descripción", start, end);
        
        UserApp creator = new UserApp();
        creator.setId("user123");
        creator.setName("Alice");
        
        when(userAppService.getUserById("user123")).thenReturn(creator);
        when(votingEventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        
        var result = votingEventService.createVotingEvent(input, "user123");
        
        assertEquals("Título", result.title());
        assertEquals("user123", result.userId());
    }
    
    @Test
    void createVotingEvent_shouldThrow_whenStartAfterEnd() {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        VotingEventInput input = new VotingEventInput("Título", "Desc", start, end);
        
        assertThrows(BadRequestException.class, () ->
                votingEventService.createVotingEvent(input, "user123"));
    }
    
    @Test
    void patchVotingEvent_shouldUpdateValues() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        VotingEventInput patch = new VotingEventInput("Nuevo título", "Nueva desc", start, end);
        
        UserApp user = new UserApp();
        user.setId("user123");
        
        VotingEvent event = new VotingEvent();
        event.setId("event1");
        event.setTitle("Antiguo");
        event.setDescription("Antigua desc");
        event.setCreator(user);
        
        when(votingEventRepository.findById("event1")).thenReturn(Optional.of(event));
        when(votingEventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        
        var result = votingEventService.patchVotingEvent("event1", "user123", patch);
        
        assertEquals("Nuevo título", result.title());
        assertEquals("Nueva desc", result.description());
    }
    
    @Test
    void patchVotingEvent_shouldThrow_whenStartAfterEnd() {
        LocalDateTime start = LocalDateTime.now().plusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        VotingEventInput patch = new VotingEventInput("T", "D", start, end);
        
        UserApp user = new UserApp();
        user.setId("user123");
        
        VotingEvent event = new VotingEvent();
        event.setId("event1");
        event.setCreator(user);
        event.setStartTime(start);
        event.setEndTime(end);
        
        when(votingEventRepository.findById("event1")).thenReturn(Optional.of(event));
        
        assertThrows(IllegalArgumentException.class, () ->
                votingEventService.patchVotingEvent("event1", "user123", patch));
    }
    
    @Test
    void deleteVotingEvent_shouldSucceed_whenAuthorized() {
        UserApp creator = new UserApp();
        creator.setId("user123");
        
        VotingEvent event = new VotingEvent();
        event.setId("event1");
        event.setCreator(creator);
        
        when(votingEventRepository.findById("event1")).thenReturn(Optional.of(event));
        
        votingEventService.deleteVotingEvent("event1", "user123");
        
        verify(votingEventRepository).delete(event);
    }
    
    @Test
    void deleteVotingEvent_shouldThrow_whenUnauthorized() {
        UserApp creator = new UserApp();
        creator.setId("user456");
        
        VotingEvent event = new VotingEvent();
        event.setId("event1");
        event.setCreator(creator);
        
        when(votingEventRepository.findById("event1")).thenReturn(Optional.of(event));
        
        assertThrows(ForbiddenException.class, () ->
                votingEventService.deleteVotingEvent("event1", "user123"));
    }
    
    @Test
    void isVotingEventCreator_shouldReturnTrue_whenMatches() {
        UserApp creator = new UserApp();
        creator.setId("user123");
        
        VotingEvent event = new VotingEvent();
        event.setId("event1");
        event.setCreator(creator);
        
        when(votingEventRepository.findById("event1")).thenReturn(Optional.of(event));
        
        assertTrue(votingEventService.isVotingEventCreator("event1", "user123"));
    }
    
    @Test
    void closeVotingEvent_shouldChangeStatus_whenAuthorized() {
        UserApp creator = new UserApp();
        creator.setId("user123");
        
        VotingEvent event = new VotingEvent();
        event.setId("event1");
        event.setCreator(creator);
        event.setStatus(VotingEventStatus.OPENED);
        
        when(votingEventRepository.findById("event1")).thenReturn(Optional.of(event));
        
        votingEventService.closeVotingEvent("event1", "user123");
        
        assertEquals(VotingEventStatus.CLOSED, event.getStatus());
        verify(votingEventRepository).save(event);
    }
    
    @Test
    void openVotingEvent_shouldChangeStatus_whenAuthorized() {
        UserApp creator = new UserApp();
        creator.setId("user123");
        
        VotingEvent event = new VotingEvent();
        event.setId("event1");
        event.setCreator(creator);
        event.setStatus(VotingEventStatus.CLOSED);
        
        when(votingEventRepository.findById("event1")).thenReturn(Optional.of(event));
        
        votingEventService.openVotingEvent("event1", "user123");
        
        assertEquals(VotingEventStatus.OPENED, event.getStatus());
        verify(votingEventRepository).save(event);
    }
    
    @Test
    void isBetweenTimeRanges_shouldReturnTrue_whenNowWithin() {
        LocalDateTime now = LocalDateTime.now();
        VotingEvent event = new VotingEvent();
        event.setStartTime(now.minusHours(1));
        event.setEndTime(now.plusHours(1));
        
        assertTrue(votingEventService.isBetweenTimeRanges(event));
    }
    
    @Test
    void isBetweenTimeRanges_shouldReturnFalse_whenNowBeforeStart() {
        VotingEvent event = new VotingEvent();
        event.setStartTime(LocalDateTime.now().plusHours(1));
        
        assertFalse(votingEventService.isBetweenTimeRanges(event));
    }
    
    @Test
    void isBetweenTimeRanges_shouldReturnFalse_whenNowAfterEnd() {
        VotingEvent event = new VotingEvent();
        event.setEndTime(LocalDateTime.now().minusHours(1));
        
        assertFalse(votingEventService.isBetweenTimeRanges(event));
    }
}
