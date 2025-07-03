package com.Elecciones.elections.repository;

import com.Elecciones.elections.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long>
{
}
