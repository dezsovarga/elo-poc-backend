package com.example.elopoc.service;

import com.example.elopoc.domain.Player;
import com.example.elopoc.mappers.PlayerMapperImpl;
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
    void play() {
        PlayerDto player1 = PlayerDto.builder().elo(1500).name("player1").build();
        PlayerDto player2 = PlayerDto.builder().elo(1500).name("player2").build();

//        playerService.play(player1, player2);
        eloService.updateEloRatings(player1, player2, 30, true);

        assertTrue(player1.getElo() == 1515);
        assertTrue(player2.getElo() == 1485);

    }
}