package com.Elecciones.elections.repository;

import com.Elecciones.elections.domain.VotingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotingEventRepository extends JpaRepository<VotingEvent, String>
{
}
