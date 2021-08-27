package com.example.elopoc.domain;


import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

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

    @ManyToMany(cascade={CascadeType.MERGE })
    @JoinTable(
            name = "tournament_player",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id"))
    private Set<Player> players;
}
