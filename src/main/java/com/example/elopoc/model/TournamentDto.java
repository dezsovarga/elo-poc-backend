package com.example.elopoc.model;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentDto {

    private Long id;
    private String name;
    private Set<PlayerDto> players;
}
