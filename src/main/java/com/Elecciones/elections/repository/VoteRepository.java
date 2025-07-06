package com.Elecciones.elections.repository;

import com.Elecciones.elections.domain.Vote;
import com.Elecciones.elections.domain.VotingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long>
{
    List<Vote> findByOptionId(Long optionId);
    List<Vote> findAll();
    List<Vote> findByVotingEvent(VotingEvent votingEvent);
}
