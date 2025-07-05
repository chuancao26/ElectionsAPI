package com.Elecciones.elections.repository;


import com.Elecciones.elections.domain.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAppRepository extends JpaRepository<UserApp, String>
{
    Optional<UserApp> findById(String id);
    Optional<UserApp> findByEmail(String email);
    List<UserApp> findAll();
}
