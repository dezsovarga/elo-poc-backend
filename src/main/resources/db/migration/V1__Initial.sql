create table hibernate_sequence (next_val bigint);
insert into hibernate_sequence values ( 1 );
create table league_standing (
    id bigint not null auto_increment,
    draws integer not null,
    goals_against integer not null,
    goals_for integer not null,
    losses integer not null,
    played_games integer not null,
    points integer not null,
    wins integer not null,
    player_id bigint,
    tournament_id bigint,
    primary key (id)
);

create table player (
    id bigint not null auto_increment,
    elo integer not null,
    name varchar(255),
    primary key (id)
);

create table play_match (
    id bigint not null,
    round_number integer not null,
    score1 integer not null,
    score2 integer not null,
    player1_id bigint,
    player2_id bigint,
    tournament_id bigint,
    primary key (id)
);

create table tournament (
    id bigint not null,
    name varchar(255),
    type varchar(255),
    primary key (id)
);

alter table league_standing add constraint FK_player foreign key (player_id) references player (id);

alter table league_standing add constraint FK_league_standing_tournament foreign key (tournament_id) references tournament (id);

alter table play_match add constraint FK_player1 foreign key (player1_id) references player (id);

alter table play_match add constraint FK_player2 foreign key (player2_id) references player (id);

alter table play_match add constraint FK_play_match_tournament foreign key (tournament_id) references tournament (id);
