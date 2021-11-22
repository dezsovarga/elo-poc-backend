package com.example.elopoc.service;

import com.example.elopoc.domain.PlayMatch;
import com.example.elopoc.domain.Player;
import com.example.elopoc.domain.Tournament;
import com.example.elopoc.mappers.PlayerMapper;
import com.example.elopoc.mappers.TournamentMapper;
import com.example.elopoc.model.PlayMatchDto;
import com.example.elopoc.model.TournamentDto;
import com.example.elopoc.model.TournamentTypeEnum;
import com.example.elopoc.repository.PlayMatchRepository;
import com.example.elopoc.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayMatchServiceImpl implements PlayMatchService {

    private final PlayMatchRepository playMatchRepository;
    private final TournamentRepository tournamentRepository;
    private final PlayerService playerService;
    private final LeagueStandingService leagueStandingService;
    private final TournamentService tournamentService;
    private final TournamentMapper tournamentMapper;
    private final PlayerMapper playerMapper;

    @Override
    public PlayMatch saveMatchScore(PlayMatchDto matchDto) {
        Optional<PlayMatch> matchOptional = playMatchRepository.findById(matchDto.getId());
        if (matchOptional.isPresent()) {
            PlayMatch match = matchOptional.get();
            match.setScore1(matchDto.getScore1());
            match.setScore2(matchDto.getScore2());
            match.setPenaltyWinner(playerMapper.dtoToPlayer(matchDto.getPenaltyWinner()));
            playMatchRepository.save(match);
            return match;
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Match with id " + matchDto.getId() + " not found");
        }
    }

    @Override
    public List<PlayMatch> saveKnockoutMatches(List<PlayMatchDto> matchResults) {
        return matchResults.stream().map(this::adjustPenaltyWinnerIfNeeded)
                .map(this::saveMatchScore)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlayMatch> generateNextKnockOutRound(Tournament tournament, List<PlayMatch> playMatchRound) {
        List<Player> roundWinners;
        int actualRound = playMatchRound.get(0).getRoundNumber();
        roundWinners = playMatchRound.stream().map(playMatch -> playMatch.getPenaltyWinner() != null ? playMatch.getPenaltyWinner() :
                playMatch.getScore1() > playMatch.getScore2() ? playMatch.getPlayer1() : playMatch.getPlayer2()).collect(Collectors.toList());
        List<PlayMatch> nextRoundMatches = tournamentService.generateMatchesForPlayers(roundWinners, false, false, actualRound + 1);
        tournament.getKnockoutStage().getMatches().addAll(nextRoundMatches);
        tournamentRepository.save(tournament);
        return nextRoundMatches;
    }

    @Override
    public List<PlayMatch> saveMatchScores(long tournamentId, List<PlayMatchDto> matchDtoList) {
        List<PlayMatch> playMatchList = new ArrayList<>();
        List<PlayMatchDto> matchResults = playerService.generateMatchScore(matchDtoList);
        Optional<Tournament> tournamentOptional = tournamentRepository.findById(tournamentId);
        if (tournamentOptional.isPresent()) {
            Tournament tournament = tournamentOptional.get();
            if (tournament.getType().equals(TournamentTypeEnum.LEAGUE.value)) {
                playMatchList = matchResults.stream().map(this::saveMatchScore).collect(Collectors.toList());
                leagueStandingService.updateStandings(tournamentId);
            } else
                if (tournament.getType().equals(TournamentTypeEnum.KNOCK_OUT.value)) {
                    playMatchList = this.saveKnockoutMatches(matchResults);
                    this.generateNextKnockOutRound(tournament, playMatchList);
                }
        }
        return playMatchList;
    }

    @Override
    public TournamentDto resetTournament(long tournamentId) {
        TournamentDto tournamentDto = tournamentService.getTournamentById(tournamentId);

        if (tournamentDto.getType().equals(TournamentTypeEnum.LEAGUE.value)) {
            List<PlayMatchDto> matchResults = playerService.resetMatchScore(tournamentDto);
            matchResults.stream().map(this::saveMatchScore).collect(Collectors.toList());
            leagueStandingService.updateStandings(tournamentId);
        } else
        if (tournamentDto.getType().equals(TournamentTypeEnum.KNOCK_OUT.value)) {
            Tournament tournament = tournamentMapper.dtoToTournament(tournamentDto);
            this.resetKnockOutTournament(tournament);
        }

        return tournamentService.getTournamentById(tournamentId);
    }

    private void resetKnockOutTournament(Tournament tournament) {
        List<Player> players = new ArrayList<>();
        tournament.getKnockoutStage().getMatches().stream().filter(match -> match.getRoundNumber() == 1).forEach(match -> {
            players.add(match.getPlayer1());
            players.add(match.getPlayer2());
        });
        tournament.getKnockoutStage().getMatches().clear();
        tournament.getKnockoutStage().setMatches(tournamentService.generateMatchesForPlayers(players, false, true, 1));
        tournamentRepository.save(tournament);
    }

    private PlayMatchDto adjustPenaltyWinnerIfNeeded(PlayMatchDto playMatchDto) {
        if (playMatchDto.getScore1()!= -1 && playMatchDto.getScore1() == playMatchDto.getScore2()) {
            int random = new Random(2).nextInt();
            playMatchDto.setPenaltyWinner(random == 0 ? playMatchDto.getPlayer1() : playMatchDto.getPlayer2());
        }
        return playMatchDto;
    }
}
