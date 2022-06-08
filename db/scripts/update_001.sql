CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR NOT NULL,
    email    VARCHAR NOT NULL UNIQUE,
    phone    VARCHAR NOT NULL UNIQUE,
    password VARCHAR NOT NULL
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
    session_id INT NOT NULL REFERENCES sessions (id),
    pos_row    INT NOT NULL,
    cell       INT NOT NULL,
    user_id    INT NOT NULL REFERENCES users (id),
    date       timestamp,
    condition  boolean default false
);
