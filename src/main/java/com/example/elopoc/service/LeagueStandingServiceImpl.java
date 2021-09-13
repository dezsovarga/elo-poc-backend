package com.example.elopoc.service;

import com.example.elopoc.domain.LeagueStanding;
import com.example.elopoc.domain.PlayMatch;
import com.example.elopoc.domain.Player;
import com.example.elopoc.domain.Tournament;
import com.example.elopoc.model.PlayMatchDto;
import com.example.elopoc.model.PlayerDto;
import com.example.elopoc.repository.LeagueStandingRepository;
import com.example.elopoc.repository.PlayMatchRepository;
import com.example.elopoc.repository.PlayerRepository;
import com.example.elopoc.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LeagueStandingServiceImpl implements LeagueStandingService {

    private final PlayerRepository playerRepository;
    private final BergerTableUtils bergerTableUtils;
    private final PlayMatchRepository playMatchRepository;
    private final TournamentRepository tournamentRepository;
    private final LeagueStandingRepository leagueStandingRepository;

    @Override
    public LeagueStanding initiateLeagueStanding(PlayerDto playerdto) {
        Optional<Player> player = playerRepository.findById(playerdto.getId());
        LeagueStanding leagueStanding = LeagueStanding.builder().build();
        if (player.isPresent()) {
            leagueStanding = LeagueStanding.builder()
                    .player(player.get())
                    .playedGames(0)
                    .wins(0)
                    .draws(0)
                    .losses(0)
                    .goalsFor(0)
                    .goalsAgainst(0)
                    .points(0)
                    .build();
        }
        else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Some players not found");
        }
        return leagueStanding;
    }

    @Override
    public List<LeagueStanding> initiateLeagueStandingList(Set<PlayerDto> players) {
        return players.stream().map(this::initiateLeagueStanding).collect(Collectors.toList());
    }

    private void scheduleMatches(List<Player> players, List<PlayMatch> matchList, List<Integer> bergerTableSchedule, int round) {
        int i =0;
        while (i < bergerTableSchedule.size()) {
            matchList.add(PlayMatch.builder().player1(players.get(bergerTableSchedule.get(i)-1))
                    .player2(players.get(bergerTableSchedule.get(i+1)-1))
                    .score1(-1).score2(-1)
                    .roundNumber(round)
                    .build());
            i = i+2;
            if (matchList.size() % (players.size()/2) == 0) {
                round++;
            }
        }
    }
    @Override
    public List<PlayMatch> generateBergerTableMatches(List<Player> players) {
        List<PlayMatch> matchList = new ArrayList<>();
        List<Integer> bergerTableSchedule = bergerTableUtils.generateBergerTable(players.size());

        //tur
        this.scheduleMatches(players, matchList, bergerTableSchedule, 1);

        //retur
        Collections.reverse(players);
        scheduleMatches(players, matchList, bergerTableSchedule, players.size());
        matchList.stream().forEach(playMatchRepository::save);
        return matchList;
    }

    @Override
    public PlayMatch updateMatchScore(PlayMatchDto matchDto) {
        Optional<PlayMatch> matchOptional = playMatchRepository.findById(matchDto.getId());
        if (matchOptional.isPresent()) {
            PlayMatch match = matchOptional.get();
            match.setScore1(matchDto.getScore1());
            match.setScore2(matchDto.getScore2());
            playMatchRepository.save(match);
            return match;
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Match with id " + matchDto.getId() + " not found");
        }
    }

    @Override
    public List<LeagueStanding> generateStandings(long tournamentId) {
        Optional<Tournament> tournamentOptional = tournamentRepository.findById(tournamentId);
        if (tournamentOptional.isPresent()) {
            Tournament tournament = tournamentOptional.get();
            this.clearStandings(tournament);
            tournament.getMatches().stream().forEach(m -> this.updateStandingsForMatch(tournament, m));
            return tournament.getStandings();
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Tournament with id " + tournamentId + " not found");
        }
    }

    private void clearStandings(Tournament tournament) {
        tournament.getStandings().stream().forEach(s -> {
            s.setPlayedGames(0);
            s.setWins(0);
            s.setDraws(0);
            s.setLosses(0);
            s.setGoalsFor(0);
            s.setGoalsAgainst(0);
            s.setPoints(0);
        });
    }

    private void updateStandingsForMatch(Tournament tournament, PlayMatch match) {
        if (match.getScore1() > -1 && match.getScore2() > -1) {
            LeagueStanding standing1 = tournament.getStandings()
                    .stream().filter(s -> s.getPlayer().getId() == match.getPlayer1().getId()).findFirst().get();
            LeagueStanding standing2 = tournament.getStandings()
                    .stream().filter(s -> s.getPlayer().getId() == match.getPlayer2().getId()).findFirst().get();
            if (match.getScore1() > match.getScore2()) {
                this.updateStandingByFirstWin(standing1, standing2, match);
            }
            if (match.getScore1() < match.getScore2()) {
                this.updateStandingBySecondWin(standing1, standing2, match);
            }
            if (match.getScore1() == match.getScore2()) {
                this.updateStandingByDraw(standing1, standing2, match);
            }
            leagueStandingRepository.save(standing1);
            leagueStandingRepository.save(standing2);
        }
    }

    private void updateStandingByFirstWin(LeagueStanding standing1, LeagueStanding standing2, PlayMatch match) {
          standing1.setWins(standing1.getWins() + 1);
        standing1.setPoints(standing1.getPoints() + 3);
        standing1.setPlayedGames(standing1.getPlayedGames() + 1);
        standing1.setGoalsFor(standing1.getGoalsFor() + match.getScore1());
        standing1.setGoalsAgainst(standing1.getGoalsAgainst() + match.getScore2());

        standing2.setPlayedGames(standing2.getPlayedGames() + 1);
        standing2.setLosses(standing2.getLosses() + 1);
        standing2.setGoalsFor(standing2.getGoalsFor() + match.getScore2());
        standing2.setGoalsAgainst(standing2.getGoalsAgainst() + match.getScore1());
    }

    private void updateStandingBySecondWin(LeagueStanding standing1, LeagueStanding standing2, PlayMatch match) {
        standing1.setLosses(standing1.getLosses() + 1);
        standing1.setPlayedGames(standing1.getPlayedGames() + 1);
        standing1.setGoalsFor(standing1.getGoalsFor() + match.getScore1());
        standing1.setGoalsAgainst(standing1.getGoalsAgainst() + match.getScore2());

        standing2.setWins(standing2.getWins() + 1);
        standing2.setPoints(standing2.getPoints() + 3);
        standing2.setPlayedGames(standing2.getPlayedGames() + 1);
        standing2.setGoalsFor(standing2.getGoalsFor() + match.getScore2());
        standing2.setGoalsAgainst(standing2.getGoalsAgainst() + match.getScore1());
    }

    private void updateStandingByDraw(LeagueStanding standing1, LeagueStanding standing2, PlayMatch match) {
        standing1.setDraws(standing1.getDraws() + 1);
        standing1.setPoints(standing1.getPoints() + 1);
        standing1.setPlayedGames(standing1.getPlayedGames() + 1);
        standing1.setGoalsFor(standing1.getGoalsFor() + match.getScore1());
        standing1.setGoalsAgainst(standing1.getGoalsAgainst() + match.getScore2());

        standing2.setDraws(standing2.getDraws() + 1);
        standing2.setPoints(standing2.getPoints() + 1);
        standing2.setPlayedGames(standing2.getPlayedGames() + 1);
        standing2.setGoalsFor(standing2.getGoalsFor() + match.getScore2());
        standing2.setGoalsAgainst(standing2.getGoalsAgainst() + match.getScore1());
    }

}
