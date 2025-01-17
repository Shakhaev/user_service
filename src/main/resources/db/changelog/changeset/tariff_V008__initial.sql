CREATE TABLE tariff (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    plan varchar(64) UNIQUE NOT NULL,
    shows int DEFAULT 0 NOT NULL,
    user_id bigint NOT NULL,
    expire_period timestamptz,

    CONSTRAINT fk_user_premium_id FOREIGN KEY (user_id) REFERENCES users (id)
);
