package com.example.elopoc.model;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerDto {

    private Long id;
    private String name;
    private int elo;
}
