package com.example.elopoc.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayMatchDto {

    private PlayerDto player1;
    private PlayerDto player2;
    private int score1;
    private int score2;
}
