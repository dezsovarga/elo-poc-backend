package com.example.elopoc.service;

import com.example.elopoc.domain.PlayMatch;
import com.example.elopoc.domain.Player;
import com.example.elopoc.domain.Tournament;
import com.example.elopoc.repository.PlayMatchRepository;
import com.example.elopoc.repository.PlayerRepository;
import com.example.elopoc.repository.TournamentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LeagueStandingServiceImplTest {

    @Autowired
    LeagueStandingService leagueStandingService;

    @Autowired
    TournamentRepository tournamentRepository;

    @Autowired
    PlayMatchRepository playMatchRepository;

    @Autowired
    PlayerRepository playerRepository;

    private List<Player> generatePlayers() {
        Player player1 = Player.builder().elo(1500).name("player1").build();
        Player player2 = Player.builder().elo(1500).name("player2").build();
        Player player3 = Player.builder().elo(1500).name("player3").build();
        Player player4 = Player.builder().elo(1500).name("player4").build();
        Player player5 = Player.builder().elo(1500).name("player5").build();
        Player player6 = Player.builder().elo(1500).name("player6").build();

        List<Player> players = Arrays.asList(player1, player2, player3, player4, player5, player6);
        players.stream().forEach(playerRepository::save);
        return players;
    }

    @Test
    void generateBergerTableMatches() {
        List<Player> players = this.generatePlayers();

        Tournament tournament = Tournament.builder().id(1L).name("Tournament name").type("league").build();
        tournamentRepository.save(tournament);
        tournament = tournamentRepository.findById(tournament.getId()).get();

        List<PlayMatch> matches = leagueStandingService.generateBergerTableMatches(players);

        assertTrue(matches.get(0).getPlayer1().getName().equals(players.get(0).getName()));
        assertTrue(matches.get(0).getPlayer2().getName().equals(players.get(5).getName()));
        assertTrue(matches.get(0).getRoundNumber() == 1);


        assertTrue(matches.get(matches.size()-1).getPlayer1().getName().equals(players.get(1).getName()));
        assertTrue(matches.get(matches.size()-1).getPlayer2().getName().equals(players.get(5).getName()));
        assertTrue(matches.get(matches.size()-1).getRoundNumber() == 10);


    }
}