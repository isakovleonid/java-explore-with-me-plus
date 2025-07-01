CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR NOT NULL UNIQUE,
    name  VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS locations
(
    id  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    lat FLOAT NOT NULL,
    lon FLOAT NOT NULL
);

CREATE TABLE IF NOT EXISTS events
(
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    annotation        VARCHAR(2000)                     NOT NULL,
    category_id       BIGINT REFERENCES categories (id) NOT NULL,
    createdOn         TIMESTAMP                         NOT NULL,
    description       VARCHAR(7000)                     NOT NULL,
    eventDate         TIMESTAMP                         NOT NULL,
    location_id       BIGINT REFERENCES locations (id)  NOT NULL,
    paid              BOOLEAN DEFAULT false             NOT NULL,
    participantLimit  INT     DEFAULT 0                 NOT NULL,
    publishedOn       TIMESTAMP                         NOT NULL,
    state             VARCHAR DEFAULT 'PENDING'         NOT NULL CHECK (state IN ('PENDING', 'PUBLISHED', 'CANCELLED')),
    requestModeration BOOLEAN DEFAULT true              NOT NULL,
    title             VARCHAR(170)                      NOT NULL,
    initiator_id      BIGINT REFERENCES users (id)      NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations
(
    id       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_id BIGINT REFERENCES events (id) NOT NULL,
    pined    BOOLEAN DEFAULT false         NOT NULL,
    title    varchar(50)                   NOT NULL
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_id     BIGINT REFERENCES events (id) NOT NULL,
    requester_id BIGINT REFERENCES users (id)  NOT NULL,
    status       VARCHAR DEFAULT 'PENDING'     NOT NULL CHECK ( status IN ('PENDING', 'REJECTED', 'CONFIRMED')),
    created      TIMESTAMP                     NOT NULL
);