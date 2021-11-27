package com.example.elopoc.domain;


import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Tournament {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "tournament_id")
    private List<LeagueGroup> groups;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private KnockoutStage knockoutStage;
    private String type;
}
