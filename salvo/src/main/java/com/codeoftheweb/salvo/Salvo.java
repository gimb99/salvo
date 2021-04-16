package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;

@Entity
public class Salvo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private int turn;

    @ElementCollection
    @Column(name = "locations")
    private List<String> salvoLocations;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayers;

    //Constructors//
    public Salvo(){
    }

    public Salvo(int turn, GamePlayer gamePlayers, List<String> locations){
        this();
        this.turn = turn;
        this.gamePlayers = gamePlayers;
        this.salvoLocations = locations;
    }

    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setSalvoes((Set<Salvo>) this);
    }

    //Getters & setters//
    //Id
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    //gamePlayers
    public GamePlayer getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(GamePlayer gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    //turn
    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    //salvoLocations
    public List<String> getSalvoLocations() {
        return salvoLocations;
    }

    public void setSalvoLocations(List<String> locations) {
        this.salvoLocations = locations;
    }

    //DTOs
    public Map<String, Object> makeSalvoDTO(){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("gamePlayer", this.getGamePlayers().getId()); //id de player
        dto.put("turn", this.getTurn());
        dto.put("locations", this.salvoLocations);
        return dto;
    }
}
