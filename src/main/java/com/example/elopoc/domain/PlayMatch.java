package com.example.elopoc.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class PlayMatch {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private Player player1;

    @OneToOne
    private Player player2;

    private int score1 = -1;
    private int score2 = -1;

    private int roundNumber;
}
