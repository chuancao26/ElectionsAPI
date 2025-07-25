package com.Elecciones.elections.service;

import com.Elecciones.elections.Exception.ConflictException;
import com.Elecciones.elections.Exception.ForbiddenException;
import com.Elecciones.elections.domain.*;
import com.Elecciones.elections.repository.ParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParticipantServiceTest {
    
    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private UserAppService userAppService;
    @Mock
    private VotingEventService votingEventService;
    
    @InjectMocks
    private ParticipantService participantService;
    
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void getMyParticipations_shouldReturnParticipantOutList() {
        UserApp user = new UserApp();
        user.setId("user1");
        
        Participant participant = new Participant();
        participant.setUser(user);
        participant.setVotingEvent(new VotingEvent());
        participant.setStatus(Status.PENDING);
        
        when(userAppService.getUserById("user1")).thenReturn(user);
        when(participantRepository.findAllByUser(user)).thenReturn(List.of(participant));
        
        var result = participantService.getMyParticipations("user1");
        
        assertEquals(1, result.size());
        assertEquals(Status.PENDING, result.get(0).status());
    }
    
    @Test
    void getParticipantOutById_shouldReturn_whenOwnerOrCreator() {
        UserApp user = new UserApp();
        user.setId("user1");
        
        VotingEvent event = new VotingEvent();
        event.setId("event1");
        
        Participant participant = new Participant();
        participant.setId(10L);
        participant.setUser(user);
        participant.setVotingEvent(event);
        
        when(participantRepository.findById(10L)).thenReturn(Optional.of(participant));
        when(votingEventService.isVotingEventCreator("event1", "user1")).thenReturn(false);
        
        var result = participantService.getParticipantOutById(10L, "user1");
        
        assertEquals("user1", result.userId());
    }
    
    @Test
    void getParticipantOutById_shouldThrow_whenNotAuthorized() {
        UserApp user = new UserApp();
        user.setId("userX");
        
        VotingEvent event = new VotingEvent();
        event.setId("eventX");
        
        Participant participant = new Participant();
        participant.setId(10L);
        participant.setUser(user);
        participant.setVotingEvent(event);
        
        when(participantRepository.findById(10L)).thenReturn(Optional.of(participant));
        when(votingEventService.isVotingEventCreator("eventX", "otherUser")).thenReturn(false);
        
        assertThrows(ForbiddenException.class,
                () -> participantService.getParticipantOutById(10L, "otherUser"));
    }
    
    @Test
    void createParticipant_shouldSucceed_whenNew() {
        String eventId = "e1", userId = "u1";
        
        UserApp user = new UserApp();
        user.setId(userId);
        
        VotingEvent event = new VotingEvent();
        event.setId(eventId);
        event.setStatus(VotingEventStatus.OPENED);
        
        when(votingEventService.isVotingEventCreator(eventId, userId)).thenReturn(false);
        when(votingEventService.getVotingEventById(eventId)).thenReturn(event);
        when(userAppService.getUserById(userId)).thenReturn(user);
        when(participantRepository.findByUserAndVotingEvent(user, event)).thenReturn(Optional.empty());
        when(participantRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        
        var result = participantService.createParticipant(eventId, userId);
        
        assertEquals(Status.PENDING, result.status());
        assertEquals(userId, result.userId());
    }
    
    @Test
    void createParticipant_shouldReturnExisting_ifAlreadyExists() {
        String eventId = "e1", userId = "u1";
        
        UserApp user = new UserApp();
        user.setId(userId);
        
        VotingEvent event = new VotingEvent();
        event.setId(eventId);
        event.setStatus(VotingEventStatus.OPENED);
        
        Participant existing = new Participant();
        existing.setUser(user);
        existing.setVotingEvent(event);
        existing.setStatus(Status.VOTED);
        
        when(votingEventService.isVotingEventCreator(eventId, userId)).thenReturn(false);
        when(votingEventService.getVotingEventById(eventId)).thenReturn(event);
        when(userAppService.getUserById(userId)).thenReturn(user);
        when(participantRepository.findByUserAndVotingEvent(user, event))
                .thenReturn(Optional.of(existing));
        
        var result = participantService.createParticipant(eventId, userId);
        
        assertEquals(Status.VOTED, result.status());
    }
    
    @Test
    void createParticipant_shouldThrow_ifEventClosed() {
        VotingEvent event = new VotingEvent();
        event.setId("e1");
        event.setStatus(VotingEventStatus.CLOSED);
        
        when(votingEventService.isVotingEventCreator("e1", "u1")).thenReturn(false);
        when(votingEventService.getVotingEventById("e1")).thenReturn(event);
        
        assertThrows(ConflictException.class, () ->
                participantService.createParticipant("e1", "u1"));
    }
    
    @Test
    void setBanParticipant_shouldSetStatusToBanned() {
        VotingEvent event = new VotingEvent();
        event.setId("e1");
        event.setTitle("Test Event");
        event.setStatus(VotingEventStatus.OPENED);
        
        UserApp user = new UserApp();
        user.setId("u1");
        user.setName("Usuario Test");
        
        Participant p = new Participant();
        p.setId(100L);
        p.setVotingEvent(event);
        p.setUser(user);
        p.setStatus(Status.PENDING);
        
        when(participantRepository.findById(100L)).thenReturn(Optional.of(p));
        when(votingEventService.isVotingEventCreator("e1", "creator1")).thenReturn(true);
        when(participantRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        
        var result = participantService.setBanParticipant(100L, "creator1");
        
        assertEquals(Status.BANNED, result.status());
        assertEquals("u1", result.userId());
        assertEquals("Test Event", result.votingTitle());
    }

    
    @Test
    void validParticipantAndVote_shouldThrow_ifBanned() {
        UserApp user = new UserApp();
        VotingEvent event = new VotingEvent();
        
        Participant banned = new Participant();
        banned.setStatus(Status.BANNED);
        
        when(participantRepository.findByUserAndVotingEvent(user, event)).thenReturn(Optional.of(banned));
        
        assertThrows(ConflictException.class, () ->
                participantService.validParticipantAndVote(user, event));
    }
    
    @Test
    void validParticipantAndVote_shouldThrow_ifAlreadyVoted() {
        UserApp user = new UserApp();
        VotingEvent event = new VotingEvent();
        
        Participant voted = new Participant();
        voted.setStatus(Status.VOTED);
        
        when(participantRepository.findByUserAndVotingEvent(user, event)).thenReturn(Optional.of(voted));
        
        assertThrows(ConflictException.class, () ->
                participantService.validParticipantAndVote(user, event));
    }
    
    @Test
    void markAsVoted_shouldChangeStatusToVoted() {
        Participant p = new Participant();
        p.setStatus(Status.PENDING);
        
        participantService.markAsVoted(p);
        
        assertEquals(Status.VOTED, p.getStatus());
        verify(participantRepository).save(p);
    }
    
    @Test
    void isParticipant_shouldReturnTrue_whenUserIsInList() {
        UserApp user = new UserApp();
        user.setId("u1");
        
        VotingEvent event = new VotingEvent();
        event.setId("e1");
        
        Participant p = new Participant();
        p.setUser(user);
        
        when(votingEventService.getVotingEventById("e1")).thenReturn(event);
        when(participantRepository.findByVotingEvent(event)).thenReturn(List.of(p));
        
        assertTrue(participantService.isParticipant("u1", "e1"));
    }
    
    @Test
    void isParticipant_shouldReturnFalse_whenUserNotFound() {
        VotingEvent event = new VotingEvent();
        event.setId("e1");
        
        when(votingEventService.getVotingEventById("e1")).thenReturn(event);
        when(participantRepository.findByVotingEvent(event)).thenReturn(List.of());
        
        assertFalse(participantService.isParticipant("u1", "e1"));
    }
}
