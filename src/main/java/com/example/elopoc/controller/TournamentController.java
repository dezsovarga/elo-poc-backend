package com.example.elopoc.controller;


import com.example.elopoc.domain.Player;
import com.example.elopoc.domain.Tournament;
import com.example.elopoc.mappers.TournamentMapper;
import com.example.elopoc.model.PlayMatchDto;
import com.example.elopoc.model.PlayerDto;
import com.example.elopoc.model.TournamentDto;
import com.example.elopoc.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/")
@CrossOrigin(origins="http://localhost:4200")
public class TournamentController {

    private final TournamentService tournamentService;
    private final TournamentMapper tournamentMapper;

    @GetMapping(produces = { "application/json" }, path = "tournament")
    public List<TournamentDto> listTournaments() {
        return tournamentService.listTournaments();
    }

    @PostMapping(produces = { "application/json" }, path = "tournament")
    public TournamentDto saveTournament(@RequestBody TournamentDto tournamentDto) {

        Tournament tournament = tournamentMapper.dtoToTournament(tournamentDto);
        return tournamentMapper.tournamentToDto(tournamentService.createNewTournament(tournament));
    }
}
