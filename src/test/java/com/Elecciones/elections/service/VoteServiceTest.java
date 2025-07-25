package com.Elecciones.elections.service;

import com.Elecciones.elections.Exception.BadRequestException;
import com.Elecciones.elections.Exception.ConflictException;
import com.Elecciones.elections.Exception.ForbiddenException;
import com.Elecciones.elections.domain.*;
import com.Elecciones.elections.dto.MyVote;
import com.Elecciones.elections.dto.VoteOut;
import com.Elecciones.elections.repository.VoteRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VoteServiceTest {
    
    @Mock private VoteRepository voteRepository;
    @Mock private UserAppService userAppService;
    @Mock private VotingEventService votingEventService;
    @Mock private ParticipantService participantService;
    @Mock private OptionService optionService;
    
    @InjectMocks private VoteService voteService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void createVote_shouldCreateVoteSuccessfully() {
        String creatorId = "user1";
        Long optionId = 1L;
        
        VotingEvent event = new VotingEvent();
        event.setId("e1");
        event.setStartTime(LocalDateTime.now().minusDays(1));
        event.setEndTime(LocalDateTime.now().plusDays(1));
        
        Option option = new Option();
        option.setId(optionId);
        option.setLabel("Option A");
        option.setVotingEvent(event);
        
        UserApp user = new UserApp();
        user.setId(creatorId);
        user.setName("User Test");
        
        Participant participant = new Participant();
        participant.setId(10L);
        participant.setUser(user);
        participant.setVotingEvent(event);
        participant.setStatus(Status.PENDING);
        
        Vote vote = new Vote(100L, user, option, LocalDateTime.now());
        
        when(optionService.getOptionById(optionId)).thenReturn(option);
        when(userAppService.getUserById(creatorId)).thenReturn(user);
        when(votingEventService.isBetweenTimeRanges(event)).thenReturn(true);
        when(participantService.validParticipantAndVote(user, event)).thenReturn(participant);
        when(voteRepository.save(any())).thenReturn(vote);
        
        VoteOut result = voteService.createVote(optionId, creatorId);
        
        assertEquals(100L, result.id());
        assertEquals("user1", result.userId());
        assertEquals("Option A", result.optionName());
        
        verify(participantService).markAsVoted(participant);
    }
    
    @Test
    void createVote_shouldThrowConflictException_whenTimeInvalid() {
        Option option = new Option();
        VotingEvent event = new VotingEvent();
        event.setId("e1");
        option.setVotingEvent(event);
        
        when(optionService.getOptionById(1L)).thenReturn(option);
        when(userAppService.getUserById("u1")).thenReturn(new UserApp());
        when(votingEventService.isBetweenTimeRanges(event)).thenReturn(false);
        
        assertThrows(ConflictException.class, () -> {
            voteService.createVote(1L, "u1");
        });
    }
    
    @Test
    void getVoteOutById_shouldReturnVote() {
        Vote vote = new Vote();
        vote.setId(10L);
        vote.setVotedAt(LocalDateTime.now());
        
        UserApp user = new UserApp();
        user.setId("u1");
        user.setName("Alice");
        
        Option option = new Option();
        option.setLabel("Option A");
        
        VotingEvent event = new VotingEvent();
        event.setTitle("Elección 2024");
        option.setVotingEvent(event);
        
        vote.setVoter(user);
        vote.setOption(option);
        
        when(voteRepository.findById(10L)).thenReturn(Optional.of(vote));
        
        MyVote result = voteService.getVoteOutById(10L, "u1");
        
        assertEquals("Elección 2024", result.nameVotingEvent());
        assertEquals("Option A", result.optionName());
    }
    
    @Test
    void getVoteOutById_shouldThrowForbidden_whenUserMismatch() {
        Vote vote = new Vote();
        UserApp user = new UserApp();
        user.setId("owner");
        vote.setVoter(user);
        
        when(voteRepository.findById(10L)).thenReturn(Optional.of(vote));
        
        assertThrows(ForbiddenException.class, () -> {
            voteService.getVoteOutById(10L, "intruder");
        });
    }
    
    @Test
    void getAllByVotingEvent_shouldReturnVotes() {
        VotingEvent event = new VotingEvent();
        event.setId("e1");
        
        UserApp creator = new UserApp();
        creator.setId("u1");
        creator.setName("Creador");
        event.setCreator(creator); // <-- AÑADE ESTA LÍNEA
        
        Option option = new Option();
        option.setId(1L);
        option.setLabel("Option A");
        option.setVotingEvent(event);
        
        UserApp user = new UserApp();
        user.setId("u1");
        user.setName("Tester");
        
        Vote vote = new Vote(99L, user, option, LocalDateTime.now());
        
        when(votingEventService.getVotingEventById("e1")).thenReturn(event);
        when(voteRepository.findByOption_VotingEvent(event)).thenReturn(List.of(vote));
        
        List<VoteOut> result = voteService.getAllByVotingEvent("e1", "u1");
        
        assertEquals(1, result.size());
        assertEquals("Option A", result.get(0).optionName());
        assertEquals("u1", result.get(0).userId());
    }

    
    @Test
    void getAllMyVotes_shouldReturnVotesOfUser() {
        UserApp user = new UserApp();
        user.setId("u1");
        
        VotingEvent event = new VotingEvent();
        event.setTitle("Elección General");
        
        Option option = new Option();
        option.setLabel("Opción X");
        option.setVotingEvent(event);
        
        Vote vote = new Vote(5L, user, option, LocalDateTime.now());
        
        when(voteRepository.findAll()).thenReturn(List.of(vote));
        
        List<MyVote> result = voteService.getAllMyVotes("u1");
        
        assertEquals(1, result.size());
        assertEquals("Opción X", result.get(0).optionName());
        assertEquals("Elección General", result.get(0).nameVotingEvent());
    }
    
    @Test
    void getVoteById_shouldThrow_whenNotFound() {
        when(voteRepository.findById(404L)).thenReturn(Optional.empty());
        
        assertThrows(BadRequestException.class, () -> voteService.getVoteOutById(404L, "u1"));
    }
}
