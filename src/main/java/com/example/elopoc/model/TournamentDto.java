package com.example.elopoc.model;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentDto {

    private Long id;
    private String name;
    private List<LeagueStandingDto> standings;
    private String type;
    private List<PlayMatchDto> matches;

}
