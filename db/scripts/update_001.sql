CREATE TABLE users
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR NOT NULL,
    email       VARCHAR NOT NULL UNIQUE,
    numberPhone VARCHAR NOT NULL UNIQUE,
    password    VARCHAR NOT NULL
);

CREATE TABLE sessions
(
    id      SERIAL PRIMARY KEY,
    name    text,
    text    text,
    created timestamp
);

CREATE TABLE ticket
(
    id         SERIAL PRIMARY KEY,
    ticket_id  INT NOT NULL,
    sessId INT NOT NULL REFERENCES sessions (id),
    row    INT NOT NULL,
    cell       INT NOT NULL,
    userId    INT NOT NULL REFERENCES users (id),
    date       timestamp,
    condition  boolean default false
);
