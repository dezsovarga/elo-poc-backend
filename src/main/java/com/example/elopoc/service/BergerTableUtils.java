package com.example.elopoc.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BergerTableUtils {

    private List<Integer> generateFirstRound(int numberOfPlayers) {
        List<Integer> actualRound = new ArrayList<>();

        int i=0;
        int j=numberOfPlayers-1;
        while (i<j) {
            actualRound.add(1 + i++);
            actualRound.add(1 + j--);
        }
        return actualRound;
    }

    public List<Integer> generateBergerTable(int numberOfPlayers) {
        List<Integer> lastRound = new ArrayList<>();
        List<Integer> actualRound;
        List<Integer> allRounds = new ArrayList<>();

        actualRound = generateFirstRound(numberOfPlayers);
        allRounds.addAll(actualRound);
        lastRound.addAll(actualRound);
        actualRound.clear();

        int lastPlayer;
        boolean lastPlayerHome = true;
        int j;

        //generate the rest of the rounds
        while (allRounds.size() < (numberOfPlayers - 1) * numberOfPlayers) {
            j=numberOfPlayers-1;
            lastPlayer = lastPlayerHome ? lastRound.get(1) : lastRound.get(0);
            if (lastPlayerHome) {
                actualRound.add(lastPlayer);
                actualRound.add(lastRound.get(j--));
            }
            else {
                actualRound.add(lastRound.get(j--));
                actualRound.add(lastPlayer);
            }

            while (j>1) {
                if (j==2) {
                    actualRound.add(lastRound.get(lastPlayerHome ? j-2 : j-1));
                }
                else {
                    actualRound.add(lastRound.get(j-1));
                }
                actualRound.add(lastRound.get(j));
                j = j-2;
            }
            lastPlayerHome = !lastPlayerHome;
            allRounds.addAll(actualRound);
            lastRound.clear();
            lastRound.addAll(actualRound);
            actualRound.clear();
        }
        return allRounds;
    }
}
