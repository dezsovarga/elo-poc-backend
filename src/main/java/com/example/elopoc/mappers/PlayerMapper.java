package com.example.elopoc.mappers;

import com.example.elopoc.domain.Player;
import com.example.elopoc.model.PlayerDto;
import org.mapstruct.Mapper;


@Mapper
public interface PlayerMapper {
    PlayerDto playerToDto(Player Player);

    Player dtoToPlayer(PlayerDto dto);
}
