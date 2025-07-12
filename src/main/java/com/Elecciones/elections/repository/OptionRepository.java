package com.Elecciones.elections.repository;

import com.Elecciones.elections.domain.Option;
import com.Elecciones.elections.domain.VotingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option, Long>
{
    List<Option> findByVotingEvent(VotingEvent votingEvent);
    List<Option> findAll();
}
