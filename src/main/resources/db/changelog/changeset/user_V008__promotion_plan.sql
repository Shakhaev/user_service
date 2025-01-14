CREATE TABLE promotion_plan (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    name varchar(64) UNIQUE NOT NULL,
    price integer,
    views_count integer
);