package com.example.elopoc.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Player {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private int elo;

    @ManyToMany(mappedBy = "players")
    private Set<Tournament> tournaments;
}
