package com.Elecciones.elections.repository;

import com.Elecciones.elections.domain.Participant;
import com.Elecciones.elections.domain.VotingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long>
{
    Optional<Participant> findById(Long id);
    Optional<Participant> findByUserIdAndVotingEventId(String userId, String votingEventId);
    
    List<Participant> findByVotingEvent(VotingEvent votingEvent);
}
