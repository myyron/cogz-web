CREATE TABLE app_user (
    id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
    username VARCHAR(32) NOT NULL,
    password VARCHAR(128) NOT NULL,
    first_name VARCHAR(32) NOT NULL,
    last_name VARCHAR(32) NOT NULL,
    role_type SMALLINT NOT NULL,
    enabled SMALLINT DEFAULT 1,
    PRIMARY KEY (id),
    UNIQUE (username)
);

CREATE TABLE event_log (
    id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
    ref_table VARCHAR(32) NOT NULL,
    ref_id BIGINT NOT NULL,
    event_oper SMALLINT NOT NULL,
    event_user BIGINT NOT NULL,
    event_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
