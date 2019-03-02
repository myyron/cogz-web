CREATE TABLE app_user (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    username VARCHAR(32) NOT NULL,
    password VARCHAR(128) NOT NULL,
    first_name VARCHAR(32) NOT NULL,
    last_name VARCHAR(32) NOT NULL,
    role_type SMALLINT NOT NULL,
    enabled SMALLINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (username)
);

CREATE TABLE event_log (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    ref_table VARCHAR(32) NOT NULL,
    ref_id BIGINT NOT NULL,
    event_oper SMALLINT NOT NULL,
    event_user BIGINT NOT NULL,
    event_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE fee (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    item_name VARCHAR(32) NOT NULL,
    fee_type SMALLINT NOT NULL,
    amount FLOAT NOT NULL,
    refundable SMALLINT NOT NULL,
    effectivity TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    enabled SMALLINT NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE player (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    call_sign VARCHAR(32) NOT NULL,
    first_name VARCHAR(32) NOT NULL,
    last_name VARCHAR(32) NOT NULL,
    contact_num VARCHAR(32) NOT NULL,
    enabled SMALLINT NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE gun (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    player_id BIGINT NOT NULL,
    name VARCHAR(32) NOT NULL,
    model VARCHAR(32) NOT NULL,
    gun_type SMALLINT NOT NULL,
    enabled SMALLINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (player_id) REFERENCES player (id)
);

CREATE TABLE game (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    event_desc VARCHAR(128),
    date date NOT NULL,
    enabled SMALLINT NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE game_player (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    game_id BIGINT NOT NULL,
    player_id BIGINT NOT NULL,
    time_in TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    in_by BIGINT NOT NULL,
    time_out TIMESTAMP,
    out_by BIGINT,
    enabled SMALLINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (game_id) REFERENCES game (id),
    FOREIGN KEY (player_id) REFERENCES player (id)
);

CREATE TABLE game_fee (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    game_player_id BIGINT NOT NULL,
    fee_id BIGINT NOT NULL,
    enabled SMALLINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (game_player_id) REFERENCES game_player (id),
    FOREIGN KEY (fee_id) REFERENCES fee (id)
);

CREATE TABLE game_fps (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    game_player_id BIGINT NOT NULL,
    gun_id BIGINT NOT NULL,
    fps INTEGER,
    enabled SMALLINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (game_player_id) REFERENCES game_player (id),
    FOREIGN KEY (gun_id) REFERENCES gun (id)
);

CREATE TABLE game_expense (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    game_id BIGINT NOT NULL,
    item_name VARCHAR(32) NOT NULL,
    total_cost FLOAT NOT NULL,
    enabled SMALLINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (game_id) REFERENCES game (id)
);
