package com.example.elopoc.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BergerTableUtilsTest {

    @Autowired
    BergerTableUtils bergerTableUtils;

    @Test
    void generateBergerTable() {
        int numberOfPlayers = 6;

        List<Integer> bergerTableSchedule = bergerTableUtils.generateBergerTable(numberOfPlayers);

        List<Integer> firstPart = bergerTableSchedule
                .stream()
                .limit(12)
                .collect(Collectors.toList());

        List<Integer> expectedElements = Arrays.asList(1, 6, 2, 5, 3, 4, 6, 4, 5, 3, 1, 2);
        assertTrue(firstPart.containsAll(expectedElements));
        assertTrue(expectedElements.containsAll(firstPart));

    }
}