package com.example.elopoc.service;

import com.example.elopoc.domain.PlayMatch;
import com.example.elopoc.domain.Tournament;
import com.example.elopoc.model.PlayMatchDto;
import com.example.elopoc.model.TournamentDto;

import java.util.List;

public interface PlayMatchService {

    PlayMatch saveMatchScore(PlayMatchDto matchDto);

    List<PlayMatch> saveKnockoutMatches(List<PlayMatchDto> matchResults);

    List<PlayMatch> generateNextKnockOutRound(Tournament tournament, List<PlayMatch> playMatchRound);

    List<PlayMatch> saveMatchScores(long tournamentId, List<PlayMatchDto> matchDtoList);

    void resetTournament(long tournamentId);
}
