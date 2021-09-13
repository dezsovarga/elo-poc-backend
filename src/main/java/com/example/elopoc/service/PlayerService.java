package com.example.elopoc.service;

import com.example.elopoc.model.PlayMatchDto;
import com.example.elopoc.model.PlayerDto;
import com.example.elopoc.model.TournamentDto;

import java.util.List;

public interface PlayerService {

    List<PlayerDto> listPlayers();

    PlayerDto winner(PlayerDto player1, PlayerDto player2);

    void play(PlayerDto player1, PlayerDto player2);

    List<PlayMatchDto> generatePlayMatchRound();

    List<PlayMatchDto> generateMatchScore(List<PlayMatchDto> playMatchDtoList);

    List<PlayMatchDto> resetMatchScore(TournamentDto tournament);

    List<PlayerDto> saveRatings(List<PlayMatchDto> playMatchDtoList);
}
