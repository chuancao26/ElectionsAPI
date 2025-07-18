package com.Elecciones.elections.repository;

import com.Elecciones.elections.domain.Vote;
import com.Elecciones.elections.domain.VotingEvent;
import com.Elecciones.elections.dto.OptionOut;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long>
{
    Optional<Vote> findById(Long id);
    List<Vote> findAll();
    List<Vote> findByOption_VotingEvent(VotingEvent votingEvent);
    
}
