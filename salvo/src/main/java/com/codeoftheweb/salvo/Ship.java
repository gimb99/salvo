package com.codeoftheweb.salvo;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import java.util.*;

@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String type;
    //private  List<String> cells;

    //OneToMany locations
    //locations = Ship positions
    @ElementCollection
    @Column(name = "locations")
    private List<String> locations;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayerId")
    private GamePlayer gamePlayers;

    //Constructores//
    public Ship(){
        this.locations = locations;
    }

    public Ship(String type, GamePlayer gamePlayers, ArrayList<String> locations){
        this();
        this.type = type;
        this.gamePlayers = gamePlayers;
        this.locations = locations;
        //cells = new ArrayList<>();
    }

    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setShips(this);
    }

    //Getters y setters//

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    //gamePlayerS o gamePlayer???
    public GamePlayer getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(GamePlayer gamePlayer) {
        this.gamePlayers = gamePlayer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    //locations da problemas, 25-03
    public List<String> getLocations() {
        return locations;
    }

    public void setlocations(ArrayList<String> locations) {
        this.locations = locations;
    }

    /*
    public List<String> getCells() {
        return cells;
    }
    */

    //makeShipDTO
    public Map<String, Object> makeShipDTO(){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());
        dto.put("type", this.getType());
        dto.put("locations", this.getLocations());
        return dto;
    }

}
