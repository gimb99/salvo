package com.codeoftheweb.salvo;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum shipType {
    //este enum es para simplificar los tipos de ship
    @JsonProperty("carrier")
    CARRIER,
    @JsonProperty("battleship")
    BATTLESHIP,
    @JsonProperty("submarine")
    SUBMARINE,
    @JsonProperty("destroyer")
    DESTROYER,
    @JsonProperty("patrolboat")
    PATROL_BOAT,
}