package com.Elecciones.elections.repository;

import com.Elecciones.elections.domain.UserApp;
import com.Elecciones.elections.domain.VotingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VotingEventRepository extends JpaRepository<VotingEvent, String>
{
    List<VotingEvent> findByCreator(UserApp creator);
}
