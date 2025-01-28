CREATE TABLE promotion_plan (
    name varchar(64) UNIQUE NOT NULL,
    price numeric(5, 2) NOT NULL,
    views_count integer NOT NULL
);