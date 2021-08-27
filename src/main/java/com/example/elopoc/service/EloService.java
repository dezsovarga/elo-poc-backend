package com.example.elopoc.service;

import com.example.elopoc.domain.Player;
import com.example.elopoc.model.PlayerDto;
import org.springframework.stereotype.Service;

@Service
public class EloService {

    public float probability(PlayerDto player1, PlayerDto player2)
    {
        return 1.0f * 1.0f / (1 + 1.0f *
                (float)(Math.pow(10, 1.0f *
                        (player1.getElo() - player2.getElo()) / 400)));
    }

    public void updateEloRatings(PlayerDto player1, PlayerDto player2,
                                 int K, Boolean d) {

        // To calculate the Winning
        // Probability of Player B
        float Pb = probability(player1, player2);

        // To calculate the Winning
        // Probability of Player A
        float Pa = probability(player2, player1);

        if (d != null) {
            // Case -1 When Player A wins
            // Updating the Elo Ratings
            if (d == true) {
                player1.setElo(player1.getElo() + Math.round(K * (1 - Pa)));
                player2.setElo(player2.getElo() + Math.round(K * (0 - Pb)));
            }

            // Case -2 When Player B wins
            // Updating the Elo Ratings
            else {
                player1.setElo(player1.getElo() + Math.round(K * (0 - Pa)));
                player2.setElo(player2.getElo() + Math.round(K * (1 - Pb)));
            }
        }
        else {
            player1.setElo(player1.getElo() + Math.round(K * (0.5f - Pa)));
            player2.setElo(player2.getElo() + Math.round(K * (0.5f - Pb)));
        }

    }
}
