package com.codeoftheweb.salvo;

import net.minidev.json.annotate.JsonIgnore;
import org.apache.tomcat.jni.Local;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import javax.persistence.Id;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


//A diferencia de Player, tenes que poner creationDate, el id se mantiene
//Esta clase aplica ONE-TO-MANY

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<GamePlayer> players = new HashSet<>();

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Score> scores = new HashSet<>();

    private LocalDateTime creationDate; //current date

    //Constructores//
    public Game() {
    }

    public Game(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setGame(this);
        //gamePlayer.add(gamePlayer);
    }

    //Getters y setters
    public long getId() {
        return id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public Score getScore(Game game){
        return scores.stream().
                filter(a -> a.getGame().getId() == game.getId()).findFirst().orElse(null);
    }

    public Set<GamePlayer> getGamePlayers() {
        return players;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.players = players;
    }

            // DTOs
    public Map<String, Object> makeGameDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());
        dto.put("created", this.getCreationDate());
        dto.put("gamePlayers", getGamePlayers().stream().map(GamePlayer::makeGamePlayerDTO));
        dto.put("scores", scores.stream().map(a -> a.makeScoreDTO()).collect(Collectors.toList()));
        return dto;
    }
}