package com.codeoftheweb.salvo;

import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

//Esta clase aplica MANY-TO-ONE

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    //manyToOne connections
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    @OneToMany(mappedBy = "gamePlayers", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Ship> ships = new HashSet<>();

    @OrderBy
    @OneToMany(mappedBy = "gamePlayers", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Salvo> salvoes;

    private LocalDateTime joinDate;

    //Constructors//
    public GamePlayer(){
        this.joinDate = LocalDateTime.now();
        this.ships = new HashSet<>();
        this.salvoes = new HashSet<>();
    }

    public GamePlayer(Player player, Game game){
        this.player = player;
        this.game = game;
        this.joinDate = LocalDateTime.now();
        this.ships = new HashSet<>();
        this.salvoes = new HashSet<>();
    }

    //Setters & getters//
    public long getId(){
        return id;
    }

    public LocalDateTime getJoinDate(){
        return joinDate;
    }

    @JsonIgnore
    public Game getGame(){
        return game;
    }

    public void setGame(Game game){
        this.game = game;
    }


    @JsonIgnore
    public Player getPlayer(){
        return player;
    }

    public void setPlayer(Player player){
        this.player = player;
    }

    @JsonIgnore
    public Set<Ship> getShips(){
        return ships;
    }

    public void setShips(Ship ship){
        this.ships = ships;
    }

    public void addShip(Set<Ship> ships){
        ships.forEach(ship -> {
            ship.setGamePlayers(this);
            this.ships.add(ship);
        });
    }

    public void addSalvo(Salvo salvo){
        salvo.setGamePlayers(this);
        this.salvoes.add(salvo);
    }

    /*
    public boolean hasSalvo(Salvo salvo) {
        for (Salvo salvo1 : salvoes) {
            if (salvo1.getTurn() == salvo.getTurn()) {
                return true;
            }
        }
        return false;
    }

    public void setSalvoes(Salvo salvo) {
        this.salvoes = salvoes;
    }

    public boolean placedShips(){
        return ships.size() = game.maxShipsAllowed;
    }*/

    //@JsonIgnore
    public Set<Salvo> getSalvoes() { return salvoes; }



    public void setSalvoes(Set<Salvo> salvoes) {
        this.salvoes = salvoes;
    }

    private Optional<Score> getScore() {
        return player.getScore(this.game);
    }

    //DTOs
    public Map<String, Object> makeGamePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.id);
        dto.put("player", player.makePlayerDTO());

        return dto;
    }

    //Find enemy
    public GamePlayer getOpponent(){
        return this.getGame().getGamePlayers().stream()
                .filter(gamePlayer -> gamePlayer.getId()!= this.getId())
                .findFirst().orElse(new GamePlayer());
    }

}