package com.codeoftheweb.salvo.repositories;

import java.util.List;

import com.codeoftheweb.salvo.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//Se tiene que crear cada vez que implementes algo nuevo, en este caso implementamos
//Players pero tendremos que hacer lo mismo para otros

@RepositoryRestResource
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findByUserName(@Param("username") String username);
}