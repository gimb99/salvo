package com.codeoftheweb.salvo;

import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    private double score;
    private LocalDateTime finishDate;

    //Constructores//
    public Score(){

    }

    public Score(Player player, Game game, double score, LocalDateTime finishDate){
        this.game = game;
        this.player = player;
        this.score = score;
        this.finishDate = finishDate;
    }

    //Getters y Setters//

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @JsonIgnore
    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @JsonIgnore
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public LocalDateTime getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDateTime finishDate) {
        this.finishDate = finishDate;
    }

    //DTOs?
    //Esto usa gameID, playerID, score, finishDate, e ID lol

    public Map<String, Object> makeScoreDTO(){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("score", this.getScore());
        dto.put("player", this.getPlayer().getId());
        dto.put("finishDate", this.getFinishDate());
        return dto;
    }
}
