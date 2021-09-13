package com.example.elopoc.service;

import com.example.elopoc.domain.Tournament;
import com.example.elopoc.model.CreateTournamentDto;
import com.example.elopoc.model.TournamentDto;

import java.util.List;

public interface TournamentService {

    List<TournamentDto> listTournaments();

    Tournament createNewTournament(CreateTournamentDto createTournamentDto);

    TournamentDto getTournamentById(long id);

}
