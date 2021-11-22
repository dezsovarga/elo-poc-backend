package com.example.elopoc.model;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KnockoutStageDto {

    private Long id;
    private List<PlayMatchDto> matches;
    private TournamentDto tournament;
}
