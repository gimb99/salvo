package com.codeoftheweb.salvo;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum shipType {
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