package com.example.elopoc.controller;

import com.example.elopoc.mappers.TournamentMapper;
import com.example.elopoc.model.CreateTournamentDto;
import com.example.elopoc.model.PlayMatchDto;
import com.example.elopoc.model.TournamentDto;
import com.example.elopoc.service.LeagueStandingService;
import com.example.elopoc.service.PlayerService;
import com.example.elopoc.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/")
@CrossOrigin(origins="http://localhost:4200")
public class TournamentController {

    private final PlayerService playerService;

    private final TournamentService tournamentService;
    private final TournamentMapper tournamentMapper;
    private final LeagueStandingService leagueStandingService;

    @GetMapping(produces = { "application/json" }, path = "tournament")
    public List<TournamentDto> listTournaments() {
        return tournamentService.listTournaments();
    }

    @GetMapping(produces = { "application/json" }, path = "tournament/{id}")
    public TournamentDto getTournament(@PathVariable long id) {
        return tournamentService.getTournamentById(id);
    }


    @PostMapping(produces = { "application/json" }, path = "tournament")
    public TournamentDto saveTournament(@RequestBody CreateTournamentDto createTournamentDto) {

        return tournamentMapper.tournamentToDto(tournamentService.createNewTournament(createTournamentDto));
    }

    @PostMapping(produces = { "application/json" }, path = "tournament/{tournamentId}/play-matches")
    public TournamentDto playMatches(@RequestBody List<PlayMatchDto> matchDtoList, @PathVariable long tournamentId) {

        List<PlayMatchDto> matchResults = playerService.generateMatchScore(matchDtoList);
        matchResults.stream().map(leagueStandingService::updateMatchScore).collect(Collectors.toList());
        leagueStandingService.generateStandings(tournamentId);
        return tournamentService.getTournamentById(tournamentId);
    }

    @PostMapping(produces = { "application/json" }, path = "tournament/{tournamentId}/reset")
    public TournamentDto resetTournament(@PathVariable long tournamentId) {

        List<PlayMatchDto> matchResults = playerService.resetMatchScore(tournamentService.getTournamentById(tournamentId));
        matchResults.stream().map(leagueStandingService::updateMatchScore).collect(Collectors.toList());
        leagueStandingService.generateStandings(tournamentId);
        return tournamentService.getTournamentById(tournamentId);
    }
}
