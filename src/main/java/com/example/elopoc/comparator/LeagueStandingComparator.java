package com.example.elopoc.comparator;

import com.example.elopoc.model.LeagueStandingDto;

import java.util.Comparator;

public class LeagueStandingComparator
        implements Comparator<LeagueStandingDto> {

    @Override
    public int compare(LeagueStandingDto leagueStandingDto1, LeagueStandingDto leagueStandingDto2) {

        // for comparison
        int pointsCompare = leagueStandingDto1.getPoints() >
                leagueStandingDto2.getPoints() ? 1 : leagueStandingDto1.getPoints() ==
                leagueStandingDto2.getPoints() ? 0 : -1;
        int goalsCompare = leagueStandingDto1.getGoalsFor() - leagueStandingDto1.getGoalsAgainst() >
                leagueStandingDto2.getGoalsFor() - leagueStandingDto2.getGoalsAgainst() ? 1 : -1;

        // 2-level comparison
        return (pointsCompare == 0) ? goalsCompare
                : pointsCompare;
    }
}