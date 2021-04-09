package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer {

	@Autowired
	PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}


	//Spring ve los métodos bean y los llama en el momento del inicio,
	//guardará la instancia para la inyección automática
	@Bean
	public CommandLineRunner initdata(PlayerRepository playerRepository, GameRepository gameRepository,
									  GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository,
									  SalvoRepository salvoRepository, ScoreRepository scoreRepository) {
		return (args) -> { //Acá es donde deposito nuevos usuarios con su mail

			// PLAYER //
			Player player1 = playerRepository.save(new Player("j.bauer@ctu.gov", passwordEncoder().encode("24")));
			Player player2 = playerRepository.save(new Player("c.obrian@ctu.gov", passwordEncoder().encode("42")));
			Player player3 = playerRepository.save(new Player("gkim_bauer@gmail.com", passwordEncoder().encode("kb")));
			Player player4 = playerRepository.save(new Player("t.almeida@ctu.gov", passwordEncoder().encode("mole")));
			Player player0 = playerRepository.save(new Player("N/A", passwordEncoder().encode("no")));

			// GAME //
			Game game1 = new Game(LocalDateTime.now());
			Game game2 = new Game(LocalDateTime.now().plusHours(1));
			Game game3 = new Game(LocalDateTime.now().plusHours(2));
			Game game4 = new Game(LocalDateTime.now().plusHours(3));
			Game game5 = new Game(LocalDateTime.now().plusHours(4));
			Game game6 = new Game(LocalDateTime.now().plusHours(5));
			Game game7 = new Game(LocalDateTime.now().plusHours(6));
			Game game8 = new Game(LocalDateTime.now().plusHours(7));

			gameRepository.save(game1);
			gameRepository.save(game2);
			gameRepository.save(game3);
			gameRepository.save(game4);
			gameRepository.save(game5);
			gameRepository.save(game6);
			gameRepository.save(game7);
			gameRepository.save(game8);

			// GAMEPLAYER //
				//Game 1
			GamePlayer gamePlayer1 = new GamePlayer(player1, game1);
			GamePlayer gamePlayer2 = new GamePlayer(player2, game1);
				//Game 2
			GamePlayer gamePlayer3 = new GamePlayer(player1, game2);
			GamePlayer gamePlayer4 = new GamePlayer(player2, game2);
				//Game 3
			GamePlayer gamePlayer5 = new GamePlayer(player2, game3);
			GamePlayer gamePlayer6 = new GamePlayer(player3, game3);
				//Game 4
			GamePlayer gamePlayer7 = new GamePlayer(player2, game4);
			GamePlayer gamePlayer8 = new GamePlayer(player1, game4);
				//Game 5
			GamePlayer gamePlayer9 = new GamePlayer(player3, game5);
			GamePlayer gamePlayer10 = new GamePlayer(player1, game5);
				//Game 6
			GamePlayer gamePlayer11 = new GamePlayer(player4, game6);
			GamePlayer gamePlayer12 = new GamePlayer(player0, game6); //NA
				//Game 7
			GamePlayer gamePlayer13 = new GamePlayer(player3, game7);
			GamePlayer gamePlayer14 = new GamePlayer(player0, game7); //NA
				//Game 8
			GamePlayer gamePlayer15 = new GamePlayer(player3, game8);
			GamePlayer gamePlayer16 = new GamePlayer(player1, game8);

			gamePlayerRepository.save(gamePlayer1);
			gamePlayerRepository.save(gamePlayer2);
			gamePlayerRepository.save(gamePlayer3);
			gamePlayerRepository.save(gamePlayer4);
			gamePlayerRepository.save(gamePlayer5);
			gamePlayerRepository.save(gamePlayer6);
			gamePlayerRepository.save(gamePlayer7);
			gamePlayerRepository.save(gamePlayer8);
			gamePlayerRepository.save(gamePlayer9);
			gamePlayerRepository.save(gamePlayer10);
			gamePlayerRepository.save(gamePlayer11);
			gamePlayerRepository.save(gamePlayer12);
			gamePlayerRepository.save(gamePlayer13);
			gamePlayerRepository.save(gamePlayer14);
			gamePlayerRepository.save(gamePlayer15);
			gamePlayerRepository.save(gamePlayer16);

			//SHIPS y SHIPLOCATIONS//

				//GAME 1, PLAYER A
			ArrayList<String> shipPosition1 = new ArrayList<>(Arrays.asList("H2", "H3", "H4"));
			Ship ship1 = shipRepository.save(new Ship("Destroyer", gamePlayer1, shipPosition1));
			ArrayList<String> shipPosition2 = new ArrayList<>(Arrays.asList("E1", "F1", "G1"));
			Ship ship2 = shipRepository.save(new Ship("Submarine", gamePlayer1, shipPosition2));
			ArrayList<String> shipPosition3 = new ArrayList<>(Arrays.asList("B4", "B5"));
			Ship ship3 = shipRepository.save(new Ship("Patrol Boat", gamePlayer1, shipPosition3));
				//GAME 1, PLAYER B
			ArrayList<String> shipPosition4 = new ArrayList<>(Arrays.asList("B5", "C5", "D5"));
			Ship ship4 = shipRepository.save(new Ship("Destroyer", gamePlayer2, shipPosition4));
			ArrayList<String> shipPosition5 = new ArrayList<>(Arrays.asList("F1", "F2"));
			Ship ship5 = shipRepository.save(new Ship("Patrol Boat", gamePlayer2, shipPosition5));

				//GAME 2, PLAYER A
			ArrayList<String> shipPosition6 = new ArrayList<>(Arrays.asList("B5", "C5", "D5"));
			Ship ship6 = shipRepository.save(new Ship("Destroyer", gamePlayer3, shipPosition6));
			ArrayList<String> shipPosition7 = new ArrayList<>(Arrays.asList("C6", "C7"));
			Ship ship7 = shipRepository.save(new Ship("Patrol Boat", gamePlayer3, shipPosition7));
				//GAME 2, PLAYER B
			ArrayList<String> shipPosition8 = new ArrayList<>(Arrays.asList("A2", "A3", "A4"));
			Ship ship8 = shipRepository.save(new Ship("Submarine", gamePlayer4, shipPosition8));
			ArrayList<String> shipPosition9 = new ArrayList<>(Arrays.asList("G6", "H6"));
			Ship ship9 = shipRepository.save(new Ship("Patrol Boat", gamePlayer4, shipPosition9));

				//GAME 3, PLAYER A
			ArrayList<String> shipPosition10 = new ArrayList<>(Arrays.asList("B5", "C5", "D5"));
			Ship ship10 = shipRepository.save(new Ship("Destroyer", gamePlayer5, shipPosition10));
			ArrayList<String> shipPosition11 = new ArrayList<>(Arrays.asList("B5", "C5", "D5"));
			Ship ship11 = shipRepository.save(new Ship("Patrol Boat", gamePlayer5, shipPosition11));
				//GAME 3, PLAYER B
			ArrayList<String> shipPosition12 = new ArrayList<>(Arrays.asList("A2", "A3", "A4"));
			Ship ship12 = shipRepository.save(new Ship("Submarine", gamePlayer6, shipPosition12));
			ArrayList<String> shipPosition13 = new ArrayList<>(Arrays.asList("G6", "H6"));
			Ship ship13 = shipRepository.save(new Ship("Patrol Boat", gamePlayer6, shipPosition13));

				//GAME 4, PLAYER A
			ArrayList<String> shipPosition14 = new ArrayList<>(Arrays.asList("B5", "C5", "D5"));
			Ship ship14 = shipRepository.save(new Ship("Destroyer", gamePlayer7, shipPosition14));
			ArrayList<String> shipPosition15 = new ArrayList<>(Arrays.asList("C6", "C7"));
			Ship ship15 = shipRepository.save(new Ship("Patrol Boat", gamePlayer7, shipPosition15));
				//GAME 4, PLAYER B
			ArrayList<String> shipPosition16 = new ArrayList<>(Arrays.asList("A2", "A3", "A4"));
			Ship ship16 = shipRepository.save(new Ship("Submarine", gamePlayer8, shipPosition16));
			ArrayList<String> shipPosition17 = new ArrayList<>(Arrays.asList("G6", "H6"));
			Ship ship17 = shipRepository.save(new Ship("Patrol Boat", gamePlayer8, shipPosition17));

				//GAME 5, PLAYER A
			ArrayList<String> shipPosition18 = new ArrayList<>(Arrays.asList("B5", "C5", "D5"));
			Ship ship18 = shipRepository.save(new Ship("Destroyer", gamePlayer9, shipPosition18));
			ArrayList<String> shipPosition19 = new ArrayList<>(Arrays.asList("C6", "C7"));
			Ship ship19 = shipRepository.save(new Ship("Patrol Boat", gamePlayer9, shipPosition19));
				//GAME 5, PLAYER B
			ArrayList<String> shipPosition20 = new ArrayList<>(Arrays.asList("A2", "A3", "A4"));
			Ship ship20 = shipRepository.save(new Ship("Submarine", gamePlayer10, shipPosition20));
			ArrayList<String> shipPosition21 = new ArrayList<>(Arrays.asList("G6", "H6"));
			Ship ship21 = shipRepository.save(new Ship("Patrol Boat", gamePlayer10, shipPosition21));

				//GAME 6, PLAYER A
			ArrayList<String> shipPosition22 = new ArrayList<>(Arrays.asList("B5", "C5", "D5"));
			Ship ship22 = shipRepository.save(new Ship("Destroyer", gamePlayer11, shipPosition22));
			ArrayList<String> shipPosition23 = new ArrayList<>(Arrays.asList("C6", "C7"));
			Ship ship23 = shipRepository.save(new Ship("Patrol Boat", gamePlayer11, shipPosition23));

				//GAME 8, PLAYER A
			ArrayList<String> shipPosition24 = new ArrayList<>(Arrays.asList("B5", "C5", "D5"));
			Ship ship24 = shipRepository.save(new Ship("Submarine", gamePlayer15, shipPosition24));
			ArrayList<String> shipPosition25 = new ArrayList<>(Arrays.asList("C6", "C7"));
			Ship ship25 = shipRepository.save(new Ship("Patrol Boat", gamePlayer15, shipPosition25));
				//GAME 8, PLAYER B
			ArrayList<String> shipPosition26 = new ArrayList<>(Arrays.asList("A2", "A3", "A4"));
			Ship ship26 = shipRepository.save(new Ship("Submarine", gamePlayer16, shipPosition26));
			ArrayList<String> shipPosition27 = new ArrayList<>(Arrays.asList("G6", "H6"));
			Ship ship27 = shipRepository.save(new Ship("Patrol Boat", gamePlayer16, shipPosition27));


			//SALVOES//
			List<String> location1 = new ArrayList<>(Arrays.asList("B5", "C5", "F1"));
			List<String> location2 = new ArrayList<>(Arrays.asList("B4", "B5", "B6"));

			List<String> location3 = new ArrayList<>(Arrays.asList("F2", "D5"));
			List<String> location4 = new ArrayList<>(Arrays.asList("E1", "H3", "A2"));

			List<String> location5 = new ArrayList<>(Arrays.asList("A2", "A4", "G6"));
			List<String> location6 = new ArrayList<>(Arrays.asList("B5", "D5", "C7"));

			List<String> location7 = new ArrayList<>(Arrays.asList("A3", "H6"));
			List<String> location8 = new ArrayList<>(Arrays.asList("C5", "C6"));

			List<String> location9 = new ArrayList<>(Arrays.asList("G6", "H6", "A4"));
			List<String> location10 = new ArrayList<>(Arrays.asList("H1", "H2", "H3"));

			List<String> location11 = new ArrayList<>(Arrays.asList("A2", "A3", "D8"));
			List<String> location12 = new ArrayList<>(Arrays.asList("E1", "F2", "G3"));

			List<String> location13 = new ArrayList<>(Arrays.asList("A3", "A4", "F7"));
			List<String> location14 = new ArrayList<>(Arrays.asList("B5", "C6", "H1"));

			List<String> location15 = new ArrayList<>(Arrays.asList("A2", "G6", "H6"));
			List<String> location16 = new ArrayList<>(Arrays.asList("C5", "C7", "D5"));

			List<String> location17 = new ArrayList<>(Arrays.asList("A1", "A2", "A3"));
			List<String> location18 = new ArrayList<>(Arrays.asList("B5", "B6", "C7"));

			List<String> location19 = new ArrayList<>(Arrays.asList("G6", "G7", "G8"));
			List<String> location20 = new ArrayList<>(Arrays.asList("C6", "D6", "E6"));

			List<String> location21 = new ArrayList<>(Arrays.asList("H1", "H8"));

			Salvo salvo1 = new Salvo(1, gamePlayer1, location1);
			Salvo salvo2 = new Salvo(1, gamePlayer2, location2);
			Salvo salvo3 = new Salvo(2, gamePlayer3, location3);
			Salvo salvo4 = new Salvo(2, gamePlayer4, location4);
			Salvo salvo5 = new Salvo(3, gamePlayer5, location5);
			Salvo salvo6 = new Salvo(3, gamePlayer6, location6);
			Salvo salvo7 = new Salvo(4, gamePlayer7, location7);
			Salvo salvo8 = new Salvo(4, gamePlayer8, location8);
			Salvo salvo9 = new Salvo(5, gamePlayer9, location9);
			Salvo salvo10 = new Salvo(5, gamePlayer9, location10);

			salvoRepository.save(salvo1);
			salvoRepository.save(salvo2);
			salvoRepository.save(salvo3);
			salvoRepository.save(salvo4);
			salvoRepository.save(salvo5);
			salvoRepository.save(salvo6);
			salvoRepository.save(salvo7);
			salvoRepository.save(salvo8);
			salvoRepository.save(salvo9);
			salvoRepository.save(salvo10);

			//SCORES
			//Game 1
			Score score1 = scoreRepository.save(new Score(player1,game1,1,LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires"))));
			Score score2 = scoreRepository.save(new Score(player2,game1,0,LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires"))));

			//Game 2
			Score score3 = scoreRepository.save(new Score(player3,game2,0.5,LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).plusHours(1)));
			Score score4 = scoreRepository.save(new Score(player4,game2,0.5,LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).plusHours(1)));

			//Game 3
			Score score5 = scoreRepository.save(new Score(player2,game3,1,LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).plusHours(2)));
			Score score6 = scoreRepository.save(new Score(player4,game3,1,LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).plusHours(2)));

		};
	}

	@Bean
	public PasswordEncoder passwordEncoder(){
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.authorizeRequests()
				.antMatchers("/api/games", "/api/players", "/api/login").permitAll()
				.antMatchers("/api/", "/web/game.html").hasAuthority("USER")
				.antMatchers("/web/**").permitAll()
				.anyRequest().authenticated()
				.and()
				.csrf().ignoringAntMatchers("/h2-console/**")
				.and()
				.headers().frameOptions().sameOrigin();

		http.formLogin()
				.usernameParameter("name")
				.passwordParameter("pwd")
				.loginPage("/api/login").permitAll()
				.defaultSuccessUrl("/web/games.html")
				.and()
				.logout().logoutUrl("/api/logout");

		// turn off checking for CSRF tokens
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

		}
	}
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {
	@Autowired
	PlayerRepository playerRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputName-> {
			Player player = playerRepository.findByUserName(inputName);
			if (player != null) {
				return new User(player.getUserName(), player.getPassword(),
						AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Unknown user: " + inputName);
			}
		});
	}
}