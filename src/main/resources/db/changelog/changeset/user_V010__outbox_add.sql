CREATE TABLE IF NOT EXISTS outbox_event
(
    id             BIGINT PRIMARY KEY,
    aggregate_id   BIGINT       NOT NULL,
    aggregate_type VARCHAR(255) NOT NULL,
    event_type     VARCHAR(255) NOT NULL,
    payload        TEXT         NOT NULL,
    created_at     TIMESTAMP    NOT NULL,
    processed      BOOLEAN      NOT NULL DEFAULT FALSE
);
