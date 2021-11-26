package com.example.elopoc.service;

import com.example.elopoc.comparator.LeagueStandingComparator;
import com.example.elopoc.domain.*;
import com.example.elopoc.mappers.PlayerMapper;
import com.example.elopoc.mappers.TournamentMapper;
import com.example.elopoc.model.*;
import com.example.elopoc.repository.KnockoutStageRepository;
import com.example.elopoc.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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

    private List<LeagueGroup> createAllLeagueGroupsFromPlayers(Set<PlayerDto> playersDto) {
        final AtomicInteger counter = new AtomicInteger();
        int chunkSize = 4;

        final Collection<List<PlayerDto>> subSets = playersDto.stream()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / chunkSize))
                .values();

        return subSets.stream().map(group ->
                this.createLeagueGroupFromPlayers(new HashSet<>(group))).collect(Collectors.toList());
    }

    private LeagueGroup createLeagueGroupFromPlayers(Set<PlayerDto> playersDto) {
        List<Player> players = playersDto.stream().map(playerMapper::dtoToPlayer).collect(Collectors.toList());
        List<LeagueStanding> leagueStandingList =
                leagueStandingService.initiateLeagueStandingList(playersDto);
        List<PlayMatch> matches = leagueStandingService.generateBergerTableMatches(players);
        return LeagueGroup.builder().standings(leagueStandingList).matches(matches).build();
    }

    @Override
    public Tournament createNewTournament(CreateTournamentDto createTournamentDto) {

        Tournament tournament;
        List<PlayMatch> matches;
        List<Player> players = createTournamentDto.getPlayers().stream().map(playerMapper::dtoToPlayer).collect(Collectors.toList());
        List<LeagueGroup> groups = new ArrayList<>();
        KnockoutStage knockoutStage = new KnockoutStage();

        if (createTournamentDto.getType() != null ) {
            if (createTournamentDto.getType().equals(TournamentTypeEnum.LEAGUE.value)) {
                groups.add(this.createLeagueGroupFromPlayers(createTournamentDto.getPlayers()));
            }
            if (createTournamentDto.getType().equals(TournamentTypeEnum.KNOCK_OUT.value)) {
                matches = this.generateMatchesForPlayers(players, false, false, 1);
                knockoutStage = KnockoutStage.builder().matches(matches).build();
                knockoutStage = knockoutStageRepository.save(knockoutStage);
            }
            if (createTournamentDto.getType().equals(TournamentTypeEnum.LEAGUE_AND_KNOCK_OUT.value)) {
                groups = this.createAllLeagueGroupsFromPlayers(createTournamentDto.getPlayers());
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
            if (tournament.getType() != null) {
                if (tournament.getType().equals(TournamentTypeEnum.LEAGUE.value) ||
                        tournament.getType().equals(TournamentTypeEnum.LEAGUE_AND_KNOCK_OUT.value)) {
                    tournamentDto.getGroups().stream().forEach(
                            leagueGroupDto -> {
                                Collections.sort(leagueGroupDto.getStandings(), new LeagueStandingComparator());
                                Collections.reverse(leagueGroupDto.getStandings());
                                leagueGroupDto.setStandings(leagueGroupDto.getStandings());
                            }
                    );
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

        List<Player> first = new ArrayList<>(players.subList(0, (players.size() + 1)/2));
        List<Player> second = new ArrayList<>(players.subList((players.size() + 1)/2, players.size()));
        Collections.reverse(second);
        List<PlayMatch> matches = new ArrayList<>();
        if (random) {
            Collections.shuffle(players);
        }
        if (players.size() > 1) {
            int i = 0;
            while (i < first.size()) {
                PlayMatch match = PlayMatch.builder()
                        .player1(first.get(i))
                        .player2(second.get(i))
                        .roundNumber(roundNumber)
                        .score1(-1).score2(-1)
                        .build();
                matches.add(match);
                i++;
            }
        }
        return matches;
    }

    @Override
    public boolean isGroupStageComplete(Tournament tournament) {
        List<LeagueGroup> incompleteGroups = new ArrayList<>();
        tournament.getGroups().stream().forEach(
                group -> {
                    boolean incompleteGroup = group.getMatches()
                            .stream()
                            .anyMatch(playMatch -> playMatch.getScore1() == -1 || playMatch.getScore2() == -1);
                    if (incompleteGroup) {
                        incompleteGroups.add(group);
                    }
                });
        return incompleteGroups.isEmpty();
    }

    @Override
    public List<Player> getQualifiedPlayers(Tournament tournament, int place) {
        TournamentDto tournamentDto = tournamentMapper.tournamentToDto(tournament);
        List<PlayerDto> qualifiedPlayers = new ArrayList<>();
        tournamentDto.getGroups().stream().forEach(
                group -> {
                    Collections.sort(group.getStandings(), new LeagueStandingComparator());
                    Collections.reverse(group.getStandings());
                    qualifiedPlayers.add(group.getStandings().get(place - 1).getPlayer());
                });
        return qualifiedPlayers.stream().map(playerMapper::dtoToPlayer).collect(Collectors.toList());
    }

    @Override
    public KnockoutStage initiateKnockoutStage(Tournament tournament) {
        List<Player> qualifiedOnFirstPlace = this.getQualifiedPlayers(tournament, 1);
        List<Player> qualifiedOnSecondPlace = this.getQualifiedPlayers(tournament, 2);
        List<Player> concatenatedList = qualifiedOnFirstPlace.stream().collect(Collectors.toList());
        concatenatedList.addAll(qualifiedOnSecondPlace);
        List<PlayMatch> matches
                = this.generateMatchesForPlayers(concatenatedList, false, false, 1);
        KnockoutStage knockoutStage = knockoutStageRepository.save(KnockoutStage.builder().matches(matches).build());
        return knockoutStage;
    }

    @Override
    public void addKnockOutStageIfMissing(Tournament tournament) {
        if (tournament.getKnockoutStage().getMatches().isEmpty()) {
            KnockoutStage knockoutStage = this.initiateKnockoutStage(tournament);
            tournament.setKnockoutStage(knockoutStage);
            tournamentRepository.save(tournament);
        }
    }
}
