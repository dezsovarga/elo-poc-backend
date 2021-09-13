package com.example.elopoc.service;

import com.example.elopoc.domain.LeagueStanding;
import com.example.elopoc.domain.PlayMatch;
import com.example.elopoc.domain.Player;
import com.example.elopoc.domain.Tournament;
import com.example.elopoc.mappers.PlayerMapper;
import com.example.elopoc.mappers.TournamentMapper;
import com.example.elopoc.model.CreateTournamentDto;
import com.example.elopoc.model.LeagueStandingDto;
import com.example.elopoc.model.TournamentDto;
import com.example.elopoc.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepository;
    private final TournamentMapper tournamentMapper;
    private final LeagueStandingService leagueStandingService;
    private final PlayerMapper playerMapper;

    @Override
    public List<TournamentDto> listTournaments() {
        return tournamentRepository.findAll().stream().map(tournamentMapper::tournamentToDto).collect(Collectors.toList());
    }

    @Override
    public Tournament createNewTournament(CreateTournamentDto createTournamentDto) {
        List<LeagueStanding> leagueStandingList =
                leagueStandingService.initiateLeagueStandingList(createTournamentDto.getPlayers());
        List<Player> players = createTournamentDto.getPlayers().stream().map(playerMapper::dtoToPlayer).collect(Collectors.toList());
        List<PlayMatch> matches = leagueStandingService.generateBergerTableMatches(players);

        Tournament tournament = Tournament.builder()
                .name(createTournamentDto.getName())
                .standings(leagueStandingList)
                .matches(matches)
                .type(createTournamentDto.getType()).build();

        return tournamentRepository.save(tournament);
    }

    @Override
    public TournamentDto getTournamentById(long id) {
        Optional<Tournament> tournamentOptional = tournamentRepository.findById(id);
        if (tournamentOptional.isPresent()) {
            Tournament tournament = tournamentOptional.get();
            TournamentDto tournamentDto = tournamentMapper.tournamentToDto(tournament);
            List<LeagueStandingDto> leagueStandingDtoList =
                    tournamentDto.getStandings()
                            .stream()
                            .sorted(Comparator.comparingInt(LeagueStandingDto::getPoints).reversed())
                            .collect(Collectors.toList());
            tournamentDto.setStandings(leagueStandingDtoList);
            return tournamentDto;
        }
        else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Tournament not found");
        }
    }
}
