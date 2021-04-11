package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.*;
import com.codeoftheweb.salvo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private SalvoRepository salvoRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    //Authentication
    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    //@RequestMapping
    @GetMapping("/games")
    public Map<String, Object> getGames(Authentication authentication){
        Map<String, Object> dto = new LinkedHashMap<>();
        if(!isGuest(authentication)){
            dto.put("player", playerRepository.
                    findByUserName(authentication.getName()).makePlayerDTO());
        }
        else{
            dto.put("player", "Guest");
        }
        dto.put("games", gameRepository.findAll()
                .stream().map(game -> game.makeGameDTO())
                .collect(Collectors.toList()));
        return dto;
    }

    @GetMapping("/players")
    public List<Object> getPlayers(){
        return playerRepository.findAll()
                .stream().map(player -> player.makePlayerDTO())
                .collect(Collectors.toList());
    }

    @GetMapping("/salvoes")
    public List<Object> getSalvoes(){
        return salvoRepository.findAll().stream()
                .map(Salvo -> Salvo.makeSalvoDTO())
                .collect(Collectors.toList());
    }

    //@PostMapping
    @PostMapping("/players")
    public ResponseEntity<Object> registerPlayers(@RequestParam String email, @RequestParam String password){

        if(email.isEmpty() || password.isEmpty()){
            return new ResponseEntity<>(makeMap("error", "Faltan datos!"), HttpStatus.FORBIDDEN);
        }

        if(playerRepository.findByUserName(email) != null){
            return new ResponseEntity<>(makeMap("error", "Nombre ya en uso"), HttpStatus.FORBIDDEN);
        }

        playerRepository.save(new Player(email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    //Crear nuevo juego, frontend
    @PostMapping("/games")
    public ResponseEntity<Object> createGame(Authentication authentication){
        Map<String , Object> map = new HashMap<>();
        ResponseEntity response;
        if (!isGuest(authentication)){ //osea si existe el player
            Player playerX = playerRepository.findByUserName(authentication.getName());
            Game gameX = gameRepository.save(new Game(LocalDateTime.now()));

            GamePlayer gp = gamePlayerRepository.save(new GamePlayer(playerRepository.save(playerX), gameX));
            //guardo playerX para cuando se implemente en nube

            response = new ResponseEntity<>(makeMap("gpid", gp.getId()), HttpStatus.CREATED);

        }else{
            response = new ResponseEntity<>(makeMap("player", "Guest"), HttpStatus.UNAUTHORIZED);
        }
        return response;
    }

    @RequestMapping(path = "/game/{gameId}/players", method = RequestMethod.POST) // @PathVariable obtiene el valor especifico de una url como gp
    public ResponseEntity <Map <String,Object>> joinGame(@PathVariable long gameId, Authentication authentication){
        ResponseEntity<Map<String, Object>> response;

        Optional<Game> gameBox = gameRepository.findById(gameId);

        if(isGuest(authentication)) {
            response = new ResponseEntity<>(makeMap("error", "el jugador no esta autorizado"), HttpStatus.UNAUTHORIZED);
        } else if (!gameBox.isPresent()){
            response = new ResponseEntity<>(makeMap("error", "no hay juego"),HttpStatus.FORBIDDEN);
        } else if (gameBox.get().getGamePlayers().size()==2){ //2 es el maximo para salvo
            response = new ResponseEntity<>(makeMap("error", "el juego esta lleno"),HttpStatus.FORBIDDEN);
        } else{
            Player player = playerRepository.findByUserName(authentication.getName());
            GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(player, gameBox.get()));
            response = new ResponseEntity<>(makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
        }
        return response;
    }

    //En caso que no encuentre gamePlayer, respuesta automatica, sino mappea
    @GetMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> gamePlayer(@PathVariable long gamePlayerId, Authentication authentication) {
        Player currentPlayer = playerRepository.findByUserName(authentication.getName());
        Optional<GamePlayer> gp = gamePlayerRepository.findById(gamePlayerId);
        ResponseEntity<Map<String, Object>> response;
        if (gp.isEmpty()) {
            response = new ResponseEntity<>(makeMap("error", "Juego no encontrado"), HttpStatus.UNAUTHORIZED);
        } else if(isGuest(authentication)) {
            response = new ResponseEntity<>(makeMap("error", "Error de identidad"),HttpStatus.FORBIDDEN );
        } else if( currentPlayer.getId() != gp.get().getPlayer().getId()) {
            response = new ResponseEntity<>(makeMap("error", "Error de identidad"), HttpStatus.FORBIDDEN);
        } else { //Si sale todo bien
            response = new ResponseEntity<>(gp.get().game_view(), HttpStatus.OK);
        }
        return response;
    }

    private  Map<String , Object> makeMap(String key, Object value) {
        Map<String , Object> map = new HashMap<>();
        map.put(key,value);
        return map;
    }

    //task 8
    @GetMapping("/games/players/{gamePlayerId}/ships")
    public Map<String,Object> getShip(@PathVariable Long gamePlayerId){
        return makeMap("ships", gamePlayerRepository.getOne(gamePlayerId).getShips().stream()
                .map(ship -> ship.makeShipDTO()).collect(Collectors.toList()));
    }

    @PostMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String,Object>> placeShip(@PathVariable Long gamePlayerId,
                                                        Authentication authentication, @RequestBody Set<Ship> ships){
        Optional<GamePlayer> gamePlayerPlace = gamePlayerRepository.findById(gamePlayerId);
        Player currentPlayer = playerRepository.findByUserName(authentication.getName());
        ResponseEntity<Map<String,Object>> response;

        if (isGuest(authentication)){
            response = new ResponseEntity<>(makeMap("error","No hay ningun jugador loggeado"), HttpStatus.UNAUTHORIZED);
        }else if (!gamePlayerPlace.isPresent()){
            response = new ResponseEntity<>(makeMap("error","Game Player Id no existe"),HttpStatus.UNAUTHORIZED);
        }else if (gamePlayerPlace.get().getPlayer().getId()!= currentPlayer.getId()){
            response = new ResponseEntity<>(makeMap("error","El jugador no está autorizado"),HttpStatus.UNAUTHORIZED);
        }else if (gamePlayerPlace.get().getShips().size() > 0){
            response = new ResponseEntity<>(makeMap("problem","Los barcos ya fueron colocados"),HttpStatus.FORBIDDEN);
        }else {
            if (ships.size()>0){
                gamePlayerPlace.get().addShip(ships);
                gamePlayerRepository.save(gamePlayerPlace.get());
                response = new ResponseEntity<>(makeMap("OK","Completado"),HttpStatus.CREATED);
            }else{
                response = new ResponseEntity<>(makeMap("error","No enviaste ningún barco"),HttpStatus.FORBIDDEN);
            }
        }
        return response;
    }

    //task 9
    @GetMapping("/games/players/{gamePlayerId}/salvos")
    public Map<String,Object> getSalvos(@PathVariable Long gamePlayerId){
        return makeMap("salvos", gamePlayerRepository.getOne(gamePlayerId).getSalvoes().stream()
                .map(salvo -> salvo.makeSalvoDTO()).collect(Collectors.toList()));
    }

    @PostMapping("/games/players/{gamePlayerId}/salvos")
    public ResponseEntity<Map<String,Object>> sendSalvos(@PathVariable Long gamePlayerId,
                                                        Authentication authentication, @RequestBody Salvo salvos){
        Optional<GamePlayer> gamePlayerSend = gamePlayerRepository.findById(gamePlayerId);
        Player currentPlayer = playerRepository.findByUserName(authentication.getName());
        ResponseEntity<Map<String,Object>> response;

        if (isGuest(authentication)){ //no hay user loggeado
            response = new ResponseEntity<>(makeMap("error","No hay ningun jugador loggeado"), HttpStatus.UNAUTHORIZED);
        }else if (!gamePlayerSend.isPresent()){
            response = new ResponseEntity<>(makeMap("error","Game Player Id no existe"),HttpStatus.UNAUTHORIZED);
        }else if (gamePlayerSend.get().getPlayer().getId()!= currentPlayer.getId()){
            response = new ResponseEntity<>(makeMap("error","El jugador no está autorizado"),HttpStatus.UNAUTHORIZED);
        }else {
            //encuentro gameplayer de enemigo
            Optional<GamePlayer> gamePlayerEnemy = gamePlayerSend.get().getGame().getGamePlayers()
                                .stream().filter(gp -> gp.getId() != gamePlayerSend.get().getId()).findFirst();
            if(gamePlayerEnemy.isPresent()){
                if (gamePlayerSend.get().getSalvoes().size() <= gamePlayerEnemy.get().getSalvoes().size()){ //!gamePlayerSend.get().hasSalvo(salvos)

                    salvos.setTurn(gamePlayerSend.get().getSalvoes().size() + 1);

                    gamePlayerSend.get().addSalvo(salvos);
                    gamePlayerRepository.save(gamePlayerSend.get());
                    response = new ResponseEntity<>(makeMap("OK","Completado"),HttpStatus.CREATED);
                }else{
                    response = new ResponseEntity<>(makeMap("error","Hay un salvo en ese turno"),HttpStatus.FORBIDDEN);
                }
            } else{
                response = new ResponseEntity<>(makeMap("error","Falta un enemigo"),HttpStatus.FORBIDDEN);
            }

        }//agarrar gameplayer ajeno y filtrar todo lo demas, y comparar localizaciones
        return response;
    }
}