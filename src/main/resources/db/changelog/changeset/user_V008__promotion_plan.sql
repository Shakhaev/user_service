CREATE TABLE promotion_plan (
    name varchar(64) UNIQUE NOT NULL,
    price numeric,
    views_count integer
);