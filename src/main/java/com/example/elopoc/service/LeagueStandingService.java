package com.example.elopoc.service;

import com.example.elopoc.domain.LeagueStanding;
import com.example.elopoc.domain.PlayMatch;
import com.example.elopoc.domain.Player;
import com.example.elopoc.domain.Tournament;
import com.example.elopoc.model.PlayerDto;

import java.util.List;
import java.util.Set;

public interface LeagueStandingService {

    LeagueStanding initiateLeagueStanding(PlayerDto player);
    List<LeagueStanding> initiateLeagueStandingList(Set<PlayerDto> players);
    List<PlayMatch> generateBergerTableMatches(List<Player> players);
    void updateStandings(Tournament tournament);
}
