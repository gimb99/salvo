package com.codeoftheweb.salvo;

import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.util.*;

import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;

//Esta clase aplica ONE - TO MANY

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    /*private String firstName;
    private String lastName;*/
    private long id;
    private String userName;
    private String password;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    Set<GamePlayer> gamePlayers = new HashSet<>();

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    Set<Score> scores = new HashSet<>();
    //cambio GamePlayer por Score?


    //Constructores
    public Player() { }
    public Player(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    //Setters y getters
    public long getId(){
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    //Segun parece, al usar Spring no conviene usar setters porque eso ya lo configura automaticamente

    public Optional<Score> getScore(Game game){
        return scores.stream().filter(score -> score.getGame().getId() == game.getId())
                .findFirst();
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setPlayer(this);
        //gamePlayer.add(gamePlayer);
    }

    // Sets y DTOs //
    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

        // Cada vez que me piden algo de mi Player, voy a usar esto para encapsular el dato de retorno
        // Es decir, vos decidis que queres que te devuelva al front end

    public Map<String, Object> makePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());
        dto.put("email", this.getUserName());
        return dto;
    }


}
