CREATE TABLE promotion (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    user_id integer,
    event_id integer,
    promotion_plan varchar NOT NULL,
    remaining_views integer not null,
    status varchar not null,
    payment_id integer,
    foreign key (user_id) references users(id),
    foreign key (event_id) references event(id)
);