package com.Elecciones.elections.repository;

import com.Elecciones.elections.domain.Option;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<Option, Long>
{
}
