package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GameState {
    PLACESHIPS,
    WAITINGFOROPP,
    PLAY,
    WAIT,
    WON,
    TIE,
    LOST,

}
