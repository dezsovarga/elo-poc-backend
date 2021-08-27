package com.example.elopoc.mappers;

import com.example.elopoc.domain.Tournament;
import com.example.elopoc.model.PlayerDto;
import com.example.elopoc.model.TournamentDto;
import org.mapstruct.Mapper;

@Mapper(uses = {PlayerDto.class})
public interface TournamentMapper {
    TournamentDto tournamentToDto(Tournament tournament);

    Tournament dtoToTournament(TournamentDto dto);
}
