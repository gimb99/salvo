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
    private long id; //importante para poder usar el manytomany

    //Conecciones manyToOne
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    //Me falta el OnetoMany de ship (le puse ships, no recuerdo si esta bien)
    @OneToMany(mappedBy = "gamePlayers", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Ship> ships = new HashSet<>();

    //26-03 agregado
    @OneToMany(mappedBy="gamePlayers", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Salvo> salvoes = new HashSet<>();

    private LocalDateTime joinDate;
/*    private Player player;
    private Game game;*/

    //Constructores//
    public GamePlayer(){
    }

    public GamePlayer(Player player, Game game){
        this.player = player;
        this.game = game;
        this.joinDate = LocalDateTime.now();
    }

    //Setters y getters//
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

    public boolean hasSalvo(Salvo salvo) {
        for (Salvo salvo1 : salvoes) {
            if (salvo1.getTurn() == salvo.getTurn()) {
                return true;
            }
        }
        return false;
    }

  /*  public boolean placedShips(){
        return ships.size() = game.maxShipsAllowed;
    }*/

    //@JsonIgnore
    public Set<Salvo> getSalvoes() {
        return salvoes;
    }

    public void setSalvoes(Salvo salvo) {
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
       /* if(getScore()!=null){
            dto.put("scores", getScore().getScore());
            //dto.put("scores", this.getScore().getScore());
            //evita recursividad
        }*/
        return dto;
    }

    // == gameViewDTO OLD
/*    public Map<String, Object> game_view() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", game.getId());
        dto.put("created", game.getCreationDate());
        dto.put("gamePlayers", game.getGamePlayers().stream().map(gamePlayer -> makeGamePlayerDTO()).collect(Collectors.toList()));
        dto.put("ships", ships.stream().map(Ship -> Ship.makeShipDTO()).collect(Collectors.toList()));
        dto.put("salvoes", game.getGamePlayers().stream().flatMap(a -> a.getSalvoes().stream().map(b ->b.makeSalvoDTO())).collect(Collectors.toList()));
        return dto;
    }*/

    //GAMEVIEW NUEVO
    public Map<String, Object> game_view() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", game.getId());
        dto.put("created", game.getCreationDate());
        dto.put("gamePlayers", game.getGamePlayers().stream().map(gamePlayer -> makeGamePlayerDTO()).collect(Collectors.toList()));
        dto.put("ships", ships.stream().map(Ship -> Ship.makeShipDTO()).collect(Collectors.toList()));
        dto.put("salvoes", game.getGamePlayers().stream().flatMap(a -> a.getSalvoes().stream().map(b ->b.makeSalvoDTO())).collect(Collectors.toList()));

        Map<String, Object> hits = new LinkedHashMap<>();
        hits.put("self", new ArrayList<>());
        hits.put("opponent", new ArrayList<>());
        dto.put("gameState", "PLACESHIPS");

        dto.put("gamePlayers", this.getGame().getGamePlayers()
                .stream()
                .map(gamePlayer1 -> gamePlayer1.makeGamePlayerDTO())
                .collect(Collectors.toList()));
        dto.put("ships",  this.getShips()
                .stream()
                .map(ship -> ship.makeShipDTO())
                .collect(Collectors.toList()));
        dto.put("salvoes",  this.getGame().getGamePlayers()
                .stream()
                .flatMap(gamePlayer1 -> gamePlayer1.getSalvoes()
                        .stream()
                        .map(salvo -> salvo.makeSalvoDTO()))
                .collect(Collectors.toList()));
        dto.put("hits", hits);
        return dto;
    }

/*    @RequestMapping("/game_view/{nn}")
    public ResponseEntity<Map<String, Object>> getGameViewByGamePlayerID(@PathVariable Long nn, Authentication  authentication) {

        if(isGuest(authentication)){
            return new  ResponseEntity<>(makeMap("error","Paso algo"),HttpStatus.UNAUTHORIZED);
        }

        Player  player  = playerRepository.findByEmail(authentication.getName()).orElse(null);
        GamePlayer gamePlayer = gamePlayerRepository.findById(nn).orElse(null);

        if(player ==  null){
            return new  ResponseEntity<>(makeMap("error","Paso algo"),HttpStatus.UNAUTHORIZED);
        }

        if(gamePlayer ==  null ){
            return new  ResponseEntity<>(makeMap("error","Paso algo"),HttpStatus.UNAUTHORIZED);
        }

        if(gamePlayer.getPlayer().getId() !=  player.getId()){
            return new  ResponseEntity<>(makeMap("error","Paso algo"),HttpStatus.CONFLICT);
        }

        Map<String,  Object>  dto = new LinkedHashMap<>();
        Map<String, Object> hits = new LinkedHashMap<>();
        hits.put("self", new ArrayList<>());
        hits.put("opponent", new ArrayList<>());

        dto.put("id", gamePlayer.getGame().getId());
        dto.put("created",  gamePlayer.getGame().getCreated());
        dto.put("gameState", "PLACESHIPS");

        dto.put("gamePlayers", gamePlayer.getGame().getGamePlayers()
                .stream()
                .map(gamePlayer1 -> gamePlayer1.makeGamePlayerDTO())
                .collect(Collectors.toList()));
        dto.put("ships",  gamePlayer.getShips()
                .stream()
                .map(ship -> ship.makeShipDTO())
                .collect(Collectors.toList()));
        dto.put("salvoes",  gamePlayer.getGame().getGamePlayers()
                .stream()
                .flatMap(gamePlayer1 -> gamePlayer1.getSalvoes()
                        .stream()
                        .map(salvo -> salvo.makeSalvoDTO()))
                .collect(Collectors.toList()));
        dto.put("hits", hits);


        return  new ResponseEntity<>(dto,HttpStatus.OK);
    }*/


}