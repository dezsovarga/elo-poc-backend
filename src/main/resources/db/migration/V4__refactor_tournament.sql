create table knockout_stage (
    id bigint not null auto_increment,
    tournament_id bigint,
    primary key (id),
    CONSTRAINT fk_knockout_stage_tournament FOREIGN KEY (tournament_id)
        REFERENCES tournament(id)
);

create table league_group (
    id bigint not null auto_increment,
    tournament_id bigint,
    primary key (id),
    CONSTRAINT fk_league_group_tournament FOREIGN KEY (tournament_id)
        REFERENCES tournament(id)
);

ALTER TABLE league_standing DROP constraint FK_league_standing_tournament;
ALTER TABLE league_standing rename column tournament_id to league_group_id;
alter table league_standing add constraint FK_league_standing_league_group foreign key (league_group_id) references league_group(id);

ALTER TABLE play_match DROP constraint FK_play_match_tournament;
ALTER TABLE play_match rename column tournament_id to league_group_id;
ALTER TABLE play_match ADD knockout_stage_id bigint;
alter table play_match add constraint FK_play_match_league_group foreign key (league_group_id) references league_group(id);
alter table play_match add constraint FK_play_match_knockout_stage foreign key (knockout_stage_id) references knockout_stage(id);

alter table tournament add knockout_stage_id bigint null;
alter table tournament add constraint FK_tournament_knockout_stage foreign key (knockout_stage_id) references knockout_stage(id);