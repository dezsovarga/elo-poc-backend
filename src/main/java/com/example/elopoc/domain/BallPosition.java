package com.example.elopoc.domain;

import com.example.elopoc.model.PlayerDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BallPosition {

    List<MatchField> matchFieldList;
    MatchField matchField;
    PlayerDto player;
    int minutes;
}
