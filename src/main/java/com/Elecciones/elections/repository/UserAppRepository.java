package com.Elecciones.elections.repository;


import com.Elecciones.elections.domain.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAppRepository extends JpaRepository<UserApp, String>
{
}
