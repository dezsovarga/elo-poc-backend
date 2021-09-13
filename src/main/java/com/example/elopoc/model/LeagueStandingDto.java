package com.example.elopoc.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeagueStandingDto {

    private Long id;
    private PlayerDto player;
    private int playedGames;
    private int wins;
    private int draws;
    private int losses;
    private int goalsFor;
    private int goalsAgainst;
    private int points;
}
