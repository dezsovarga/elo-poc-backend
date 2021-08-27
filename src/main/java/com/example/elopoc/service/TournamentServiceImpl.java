package com.example.elopoc.service;

import com.example.elopoc.domain.Player;
import com.example.elopoc.domain.Tournament;
import com.example.elopoc.mappers.TournamentMapper;
import com.example.elopoc.mappers.TournamentMapperImpl;
import com.example.elopoc.model.TournamentDto;
import com.example.elopoc.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepository;
    private final TournamentMapper tournamentMapper;

    @Override
    public List<TournamentDto> listTournaments() {
        return tournamentRepository.findAll().stream().map(tournamentMapper::tournamentToDto).collect(Collectors.toList());
    }

    @Override
    public Tournament createNewTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }
}
