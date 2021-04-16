package com.codeoftheweb.salvo.repositories;

import java.util.List;

import com.codeoftheweb.salvo.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findByUserName(@Param("username") String username);
}