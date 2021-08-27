package com.example.elopoc.controller;

import com.example.elopoc.domain.Player;
import com.example.elopoc.model.PlayMatchDto;
import com.example.elopoc.model.PlayerDto;
import com.example.elopoc.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/")
@CrossOrigin(origins="http://localhost:4200")
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping(produces = { "application/json" }, path = "player")
    public List<PlayerDto> listPlayers() {
        return playerService.listPlayers();
    }

    @GetMapping(produces = { "application/json" }, path = "matchRound")
    public List<PlayMatchDto> generateMatchRound() {
        return playerService.generatePlayMatchRound();
    }

    @PostMapping(produces = { "application/json" }, path = "matchScore")
    public List<PlayMatchDto> generateMatchScores(@RequestBody List<PlayMatchDto> playMatchDtoList) {

        return playerService.generateMatchScore(playMatchDtoList);
    }

    @PostMapping(produces = { "application/json" }, path = "rating")
    public List<PlayerDto> saveRatings(@RequestBody List<PlayMatchDto> playMatchDtoList) {

        return playerService.saveRatings(playMatchDtoList);
    }
}
