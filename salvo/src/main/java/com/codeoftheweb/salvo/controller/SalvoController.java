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

import static com.codeoftheweb.salvo.GameState.TIE;

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

    @RequestMapping("/game_view/{nn}")
    public ResponseEntity<Map<String, Object>> getGameViewByGamePlayerID(@PathVariable Long nn, Authentication  authentication) {

        if(isGuest(authentication)){
            return new  ResponseEntity<>(makeMap("error","Usuario no autorizado"), HttpStatus.UNAUTHORIZED);
        }

        Player player  = playerRepository.findByUserName(authentication.getName());
        Optional <GamePlayer> gamePlayer = gamePlayerRepository.findById(nn);

        if(player ==  null){
            return new  ResponseEntity<>(makeMap("error","No existe el jugador"),HttpStatus.UNAUTHORIZED);
        }

        if(gamePlayer ==  null){
            return new  ResponseEntity<>(makeMap("error","Error de gameplayer"),HttpStatus.UNAUTHORIZED);
        }

        if(gamePlayer.get().getPlayer().getId() !=  player.getId()){
            return new  ResponseEntity<>(makeMap("error","Error de ID"),HttpStatus.CONFLICT);
        }

        Map<String,  Object>  dto = new LinkedHashMap<>();
        Map<String, Object> hits = new LinkedHashMap<>();
        hits.put("self",hitsAndSinks(gamePlayer.get(),gamePlayer.get().getOpponent()));
        hits.put("opponent", hitsAndSinks(gamePlayer.get().getOpponent(),gamePlayer.get()));

        dto.put("id", gamePlayer.get().getId());
        dto.put("created",  gamePlayer.get().getGame().getCreationDate());
        dto.put("gameState", gameState(gamePlayer.get()));

        dto.put("gamePlayers", gamePlayer.get().getGame().getGamePlayers()
                .stream()
                .map(gamePlayer1 -> gamePlayer1.makeGamePlayerDTO())
                .collect(Collectors.toList()));
        dto.put("ships",  gamePlayer.get().getShips()
                .stream()
                .map(ship -> ship.makeShipDTO())
                .collect(Collectors.toList()));
        dto.put("salvoes",  gamePlayer.get().getGame().getGamePlayers()
                .stream()
                .flatMap(gamePlayer1 -> gamePlayer1.getSalvoes()
                        .stream()
                        .map(salvo -> salvo.makeSalvoDTO()))
                .collect(Collectors.toList()));
        dto.put("hits", hits);

        return new ResponseEntity<>(dto,HttpStatus.OK);
    }

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

    //Create new game on front end
    @PostMapping("/games")
    public ResponseEntity<Object> createGame(Authentication authentication){
        Map<String , Object> map = new HashMap<>();
        ResponseEntity response;
        if (!isGuest(authentication)){ //Does player exist?
            Player playerX = playerRepository.findByUserName(authentication.getName());
            Game gameX = gameRepository.save(new Game(LocalDateTime.now()));

            GamePlayer gp = gamePlayerRepository.save(new GamePlayer(playerRepository.save(playerX), gameX));
            //playerX is used for cloud-based hosting

            response = new ResponseEntity<>(makeMap("gpid", gp.getId()), HttpStatus.CREATED);

        }else{
            response = new ResponseEntity<>(makeMap("player", "Guest"), HttpStatus.UNAUTHORIZED);
        }
        return response;
    }

    // @PathVariable obtiene el valor especifico de una url como gp
    @RequestMapping(path = "/game/{gameId}/players", method = RequestMethod.POST)
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

    private  Map<String , Object> makeMap(String key, Object value) {
        Map<String , Object> map = new HashMap<>();
        map.put(key,value);
        return map;
    }

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

    @GetMapping("/games/players/{gamePlayerId}/salvoes")
    public Map<String,Object> getSalvos(@PathVariable Long gamePlayerId){
        return makeMap("salvos", gamePlayerRepository.getOne(gamePlayerId).getSalvoes().stream()
                .map(salvo -> salvo.makeSalvoDTO()).collect(Collectors.toList()));
    }

    @PostMapping("/games/players/{gamePlayerId}/salvoes")
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
            //Find enemy's gameplayer
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
        }
        //Fetch the other player´s gameplayer and filter everything to compare locations
        return response;
    }

    public List<String> findShipLocations(GamePlayer gamePlayer, String type) {
        Optional<Ship> response;
        response = gamePlayer.getShips().stream().filter(ship -> ship.getType().equals(type)).findFirst();
        if (response.isEmpty()) {
            return new ArrayList<String>();
        }
        return response.get().getLocations();
    }

    private Boolean getIfAllSunk (GamePlayer self, GamePlayer opponent) {

        if(!opponent.getShips().isEmpty() && !self.getSalvoes().isEmpty()){
            return opponent.getSalvoes().stream().flatMap(salvo -> salvo.getSalvoLocations().stream())
                    .collect(Collectors.toList()).containsAll(self.getShips().stream()
                    .flatMap(ship -> ship.getLocations().stream()).collect(Collectors.toList()));
        }
        return false;
    }
    //Game state
    private GameState gameState(GamePlayer self) {
        if (self.getShips().size() == 0) { //are there any ships?
            return GameState.PLACESHIPS;
        }
        if (self.getGame().getGamePlayers().size() == 1) { //is there any opponent?
            return GameState.WAITINGFOROPP;
        }
            if (self.getOpponent().getShips().isEmpty()) {
                return GameState.WAIT;
            }
            if (self.getSalvoes().size() < self.getOpponent().getSalvoes().size()) {
                return GameState.PLAY;
            }
            if (self.getSalvoes().size() > self.getOpponent().getSalvoes().size()) {
                return GameState.WAIT;
            }
            if (self.getSalvoes().size() == self.getOpponent().getSalvoes().size()) {
                //test if there's any salvoes
                boolean selfLost = getIfAllSunk(self, self.getOpponent());
                boolean opponentLost = getIfAllSunk(self.getOpponent(), self);

                if (selfLost && opponentLost) {
                    scoreRepository.save(new Score(self.getPlayer(), self.getGame(), 0.5, LocalDateTime.now()));
                    return GameState.TIE;
                } else if (opponentLost) {
                    scoreRepository.save(new Score(self.getPlayer(), self.getGame(), 1.0, LocalDateTime.now()));
                    return GameState.WON;
                } else if (selfLost) {
                    scoreRepository.save(new Score(self.getPlayer(), self.getGame(), 0.0, LocalDateTime.now()));
                    return GameState.LOST;
                }

            } return GameState.PLAY;
        }

    private List<Map> hitsAndSinks(GamePlayer self, GamePlayer opponent) {

        List<Map> hits = new ArrayList<>();

        //TOTAL DAMAGE
        int carrierHits = 0;
        int battleshipHits = 0;
        int submarineHits = 0;
        int destroyerHits = 0;
        int patrolboatHits = 0;

        //locations
        List<String> carrierLocations = findShipLocations(self, "carrier");
        List<String> battleshipLocations = findShipLocations(self, "battleship");
        List<String> submarineLocations = findShipLocations(self, "submarine");
        List<String> destroyerLocations = findShipLocations(self, "destroyer");
        List<String> patrolboatLocations = findShipLocations(self, "patrolboat");

        for (Salvo salvo : opponent.getSalvoes()) {

            Map<String, Object> damagePerTurn = new LinkedHashMap<>();
            Map<String, Object> hitsPerTurn = new LinkedHashMap<>();
            ArrayList<String> hitCellList = new ArrayList<>();

            //missed
            int missed = salvo.getSalvoLocations().size();

            //Hits per turn
            int carrierTurn = 0;
            int battleshipTurn = 0;
            int submarineTurn = 0;
            int destroyerTurn = 0;
            int patrolboatTurn = 0;

            for (String location : salvo.getSalvoLocations()) {
                if (carrierLocations.contains(location)) {
                    carrierHits++;
                    carrierTurn++;
                    missed--;
                    hitCellList.add(location);
                }
                if (battleshipLocations.contains(location)) {
                    battleshipHits++;
                    battleshipTurn++;
                    missed--;
                    hitCellList.add(location);
                }
                if (submarineLocations.contains(location)) {
                    submarineHits++;
                    submarineTurn++;
                    missed--;
                    hitCellList.add(location);
                }
                if (destroyerLocations.contains(location)) {
                    destroyerHits++;
                    destroyerTurn++;
                    missed--;
                    hitCellList.add(location);
                }
                if (patrolboatLocations.contains(location)) {
                    patrolboatHits++;
                    patrolboatTurn++;
                    missed--;
                    hitCellList.add(location);
                }
            }
            //damage per turn
            damagePerTurn.put("carrierHits", carrierTurn);
            damagePerTurn.put("battleshipHits", battleshipTurn);
            damagePerTurn.put("submarineHits", submarineTurn);
            damagePerTurn.put("destroyerHits", destroyerTurn);
            damagePerTurn.put("patrolboatHits", patrolboatTurn);
            //add to total damage
            damagePerTurn.put("carrier", carrierHits);
            damagePerTurn.put("battleship", battleshipHits);
            damagePerTurn.put("submarine", submarineHits);
            damagePerTurn.put("destroyer", destroyerHits);
            damagePerTurn.put("patrolboat", patrolboatHits);

            hitsPerTurn.put("turn", salvo.getTurn());
            hitsPerTurn.put("missed", missed);
            hitsPerTurn.put("damages", damagePerTurn);
            hitsPerTurn.put("hitLocations", hitCellList);
            //add to List
            hits.add(hitsPerTurn);
        }
        return hits;
    }
}