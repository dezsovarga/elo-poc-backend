package com.example.elopoc.service;

import com.example.elopoc.comparator.LeagueStandingComparator;
import com.example.elopoc.domain.*;
import com.example.elopoc.mappers.PlayerMapper;
import com.example.elopoc.mappers.TournamentMapper;
import com.example.elopoc.model.CreateTournamentDto;
import com.example.elopoc.model.LeagueStandingDto;
import com.example.elopoc.model.TournamentDto;
import com.example.elopoc.model.TournamentTypeEnum;
import com.example.elopoc.repository.KnockoutStageRepository;
import com.example.elopoc.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepository;
    private final TournamentMapper tournamentMapper;
    private final LeagueStandingService leagueStandingService;
    private final PlayerMapper playerMapper;
    private final KnockoutStageRepository knockoutStageRepository;

    @Override
    public List<TournamentDto> listTournaments() {
        return tournamentRepository.findAll().stream().map(tournamentMapper::tournamentToDto).collect(Collectors.toList());
    }

    @Override
    public Tournament createNewTournament(CreateTournamentDto createTournamentDto) {

        Tournament tournament;
        List<PlayMatch> matches;
        List<Player> players = createTournamentDto.getPlayers().stream().map(playerMapper::dtoToPlayer).collect(Collectors.toList());
        List<LeagueStanding> leagueStandingList;
        List<LeagueGroup> groups = new ArrayList<>();
        KnockoutStage knockoutStage = new KnockoutStage();

        if (createTournamentDto.getType() != null ) {
            if (createTournamentDto.getType().equals(TournamentTypeEnum.LEAGUE.value)) {
                leagueStandingList =
                        leagueStandingService.initiateLeagueStandingList(createTournamentDto.getPlayers());
                matches = leagueStandingService.generateBergerTableMatches(players);
                groups.add(LeagueGroup.builder().standings(leagueStandingList).matches(matches).build());
            } else
            if (createTournamentDto.getType().equals(TournamentTypeEnum.KNOCK_OUT.value)) {
                matches = this.generateMatchesForPlayers(players, false, false, 1);
                knockoutStage = KnockoutStage.builder().matches(matches).build();
                knockoutStage = knockoutStageRepository.save(knockoutStage);
            } else
            if (createTournamentDto.getType().equals(TournamentTypeEnum.LEAGUE_AND_KNOCK_OUT.value)) {
                matches = this.generateMatchesForPlayers(players, false, false, 1);
                leagueStandingList = new ArrayList<>();
            }
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Tournament type not valid");
        }

        tournament = Tournament.builder()
                .name(createTournamentDto.getName())
                .groups(groups)
                .knockoutStage(knockoutStage)
                .type(createTournamentDto.getType())
                .type(createTournamentDto.getType()).build();

        return tournamentRepository.save(tournament);
    }

    @Override
    public TournamentDto getTournamentById(long id) {
        Optional<Tournament> tournamentOptional = tournamentRepository.findById(id);
        if (tournamentOptional.isPresent()) {
            Tournament tournament = tournamentOptional.get();
            TournamentDto tournamentDto = tournamentMapper.tournamentToDto(tournament);
            //TODO: refactor for all groups
            if (tournament.getType() != null) {
                if (tournament.getType().equals(TournamentTypeEnum.LEAGUE.value)) {
                    List<LeagueStandingDto> leagueStandingDtoList = tournamentDto.getGroups().get(0).getStandings();
                    Collections.sort(leagueStandingDtoList, new LeagueStandingComparator());
                    Collections.reverse(leagueStandingDtoList);
                    tournamentDto.getGroups().get(0).setStandings(leagueStandingDtoList);
                }
            }

            return tournamentDto;
        }
        else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Tournament not found");
        }
    }

    @Override
    public List<PlayMatch> generateMatchesForPlayers(List<Player> players, boolean two_legs, boolean random, int roundNumber) {
        List<PlayMatch> matches = new ArrayList<>();
        if (random) {
            Collections.shuffle(players);
        }
        if (players.size() > 1) {
            int i = 0;
            while (i < players.size()) {
                PlayMatch match = PlayMatch.builder()
                        .player1(players.get(i))
                        .player2(players.get(i+1))
                        .roundNumber(roundNumber)
                        .score1(-1).score2(-1)
                        .build();
                matches.add(match);
                i +=2;
            }
        }
        return matches;
    }
}
