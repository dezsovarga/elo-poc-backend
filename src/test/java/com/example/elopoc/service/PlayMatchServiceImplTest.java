package com.example.elopoc.service;

import com.example.elopoc.domain.PlayMatch;
import com.example.elopoc.domain.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PlayMatchServiceImplTest {

    @Autowired
    TournamentService tournamentService;

    @Test
    void testGenerateMatchesForPlayers() {

        Player player1 = Player.builder().elo(1500).name("player1").build();
        Player player2 = Player.builder().elo(1500).name("player2").build();
        Player player3 = Player.builder().elo(1500).name("player3").build();
        Player player4 = Player.builder().elo(1500).name("player4").build();
        Player player5 = Player.builder().elo(1500).name("player5").build();
        Player player6 = Player.builder().elo(1500).name("player6").build();
        Player player7 = Player.builder().elo(1500).name("player7").build();
        Player player8 = Player.builder().elo(1500).name("player8").build();

        List<Player> players = Arrays.asList(player1, player2, player3, player4, player5, player6, player7, player8);

        List<PlayMatch> matches = tournamentService.generateMatchesForPlayers(players, false, false, 1);

        assertTrue(!matches.isEmpty());

    }

}