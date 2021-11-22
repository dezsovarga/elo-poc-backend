package com.example.elopoc.service;

import com.example.elopoc.domain.PlayMatch;
import com.example.elopoc.domain.Player;
import com.example.elopoc.mappers.PlayerMapperImpl;
import com.example.elopoc.model.PlayMatchDto;
import com.example.elopoc.model.PlayerDto;
import com.example.elopoc.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PlayerServiceImplTest {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private EloService eloService;

    @Test
    void updateEloRatings() {
        PlayerDto player1 = PlayerDto.builder().elo(1500).name("player1").build();
        PlayerDto player2 = PlayerDto.builder().elo(1500).name("player2").build();

        playerService.play(player1, player2);

        assertTrue(player1.getElo() == 1515);
        assertTrue(player2.getElo() == 1485);

    }

//    @Test
//    void play() {
//        PlayerDto player1 = PlayerDto.builder().elo(1750).name("player1").build();
//        PlayerDto player2 = PlayerDto.builder().elo(1594).name("player2").build();
//
//        PlayMatchDto playMatch = PlayMatchDto.builder().player1(player1).score1(0).player2(player2).score2(0).build();
//
//        for (int i=0; i<40; i++) {
//            playerService.play(player1, player2);
//            assertTrue(true);
//            playMatch.setScore1(0);
//            playMatch.setScore2(0);
//        }
//
//        assertTrue(true);
//
//    }
}