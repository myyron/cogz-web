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
