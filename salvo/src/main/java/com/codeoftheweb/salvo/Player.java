package com.codeoftheweb.salvo;


import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;

import net.minidev.json.annotate.JsonIgnore;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;

//this class uses ONE - TO MANY

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")

    private long id;
    private String userName;
    private String password;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    Set<GamePlayer> gamePlayers = new HashSet<>();

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    Set<Score> scores = new HashSet<>();


    //Constructors
    public Player() { }
    public Player(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    //Setters & getters
    public long getId(){
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public Optional<Score> getScore(Game game){
        return scores.stream().filter(score -> score.getGame().getId() == game.getId())
                .findFirst();
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setPlayer(this);
    }

    // Sets & DTOs //
    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    //DTOs help you to choose what to show to the end user in the front end
    public Map<String, Object> makePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());
        dto.put("email", this.getUserName());
        return dto;
    }


}
