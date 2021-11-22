package com.example.elopoc.model;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeagueGroupDto {

    private Long id;
    private List<LeagueStandingDto> standings;
    private List<PlayMatchDto> matches;
//    private TournamentDto tournament;

}
