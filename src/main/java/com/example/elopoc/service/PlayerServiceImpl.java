package com.example.elopoc.service;

import com.example.elopoc.mappers.PlayerMapper;
import com.example.elopoc.model.PlayMatchDto;
import com.example.elopoc.model.PlayerDto;
import com.example.elopoc.model.TournamentDto;
import com.example.elopoc.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final EloService eloService;
    private final PlayerMapper playerMapper;

    @Override
    public List<PlayerDto> listPlayers() {
        return playerRepository.findAll()
                .stream()
                .map(playerMapper::playerToDto)
                .sorted(Comparator.comparingInt(PlayerDto::getElo).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public PlayerDto winner(PlayerDto player1, PlayerDto player2) {
        int upperBound = Math.max(player1.getElo(), player2.getElo()) * 110/100;
        int score1 = this.score(player1, upperBound);
        int score2 = this.score(player2, upperBound);

        return score1 > score2 ? player1 : player2;
    }

    public void play(PlayerDto player1, PlayerDto player2) {
        PlayerDto winner = this.winner(player1, player2);
        boolean player1Wins = player1.equals(winner);
        eloService.updateEloRatings(player1, player2, 30, player1Wins);
    }

    private PlayMatchDto generateMatchScore(PlayMatchDto playMatchDto) {
        int score1 = score(playMatchDto.getPlayer1(), playMatchDto.getPlayer2(), 15);
        int score2 = score(playMatchDto.getPlayer2(), playMatchDto.getPlayer1(), 15);

        playMatchDto.setScore1(score1);
        playMatchDto.setScore2(score2);
        return playMatchDto;
    }

    private PlayMatchDto resetMatchScore(PlayMatchDto playMatchDto) {
        playMatchDto.setScore1(-1);
        playMatchDto.setScore2(-1);
        return playMatchDto;
    }

    public List<PlayMatchDto> generateMatchScore(List<PlayMatchDto> playMatchDtoList) {
        return playMatchDtoList.stream().map(this::generateMatchScore).collect(Collectors.toList());
    }

    @Override
    public List<PlayMatchDto> resetMatchScore(TournamentDto tournament) {
        return tournament.getMatches().stream().map(this::resetMatchScore).collect(Collectors.toList());
    }

    @Override
    public List<PlayerDto> saveRatings(List<PlayMatchDto> playMatchDtoList) {
        playMatchDtoList.stream().forEach(match -> {
            Boolean player1Win = match.getScore1() > match.getScore2();
            if (match.getScore1() == match.getScore2()) {
                player1Win = null;
            }
            eloService.updateEloRatings(match.getPlayer1(), match.getPlayer2(), 30, player1Win);

        });
        playMatchDtoList.stream().forEach(match -> {
            playerRepository.save(playerMapper.dtoToPlayer(match.getPlayer1()));
            playerRepository.save(playerMapper.dtoToPlayer(match.getPlayer2()));
        });
        return playerRepository.findAll()
                .stream()
                .map(playerMapper::playerToDto)
                .sorted(Comparator.comparingInt(PlayerDto::getElo).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<PlayMatchDto> generatePlayMatchRound() {
        List<PlayMatchDto> playMatchList = new ArrayList<>();
        List<PlayerDto> playerList = playerRepository.findAll()
                .stream()
                .map(playerMapper::playerToDto)
                .collect(Collectors.toList());
        Collections.shuffle(playerList);
        for(int i=0; i<playerList.size(); i+=2) {
            PlayMatchDto playMatch = PlayMatchDto.builder().player1(playerList.get(i)).player2(playerList.get(i+1)).build();
            playMatchList.add(playMatch);
        }
        return playMatchList;
    }

    private boolean scoreAttempt(PlayerDto player, int upperBound) {
        Random rand = new Random();
        if (rand.nextInt(upperBound) < player.getElo()) {
            return true;
        }
        return false;
    }

    private int score(PlayerDto player, int upperBound) {
        int score=0;
        for (int i=0; i<10; i++) {
            if (scoreAttempt(player, upperBound)) {
                score++;
            }
        }
        return score;
    }

    private boolean scoreAttempt(PlayerDto playerToScore, PlayerDto playerToDefend) {
        int upperBound = Math.max(playerToScore.getElo(), playerToDefend.getElo()) * 110/100;
        boolean toScore = this.scoreAttempt(playerToScore, upperBound);
        boolean toDefend = this.scoreAttempt(playerToDefend, upperBound);

        return toScore && !toDefend;
    }

    private int score(PlayerDto playerToScore, PlayerDto playerToDefend, int iteration) {
        int score=0;
        for (int i=0; i<10; i++) {
            if (scoreAttempt(playerToScore, playerToDefend)) {
                score++;
            }
        }
        return score;
    }
}
