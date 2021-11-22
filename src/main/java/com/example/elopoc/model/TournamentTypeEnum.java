package com.example.elopoc.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TournamentTypeEnum {

    LEAGUE("league"),
    KNOCK_OUT("knock-out"),
    LEAGUE_AND_KNOCK_OUT("league/knock-out");

    public final String value;

}
