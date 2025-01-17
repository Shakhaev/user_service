CREATE TABLE promotion (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    user_id bigint,
    event_id bigint,
    promotion_plan_type varchar(20) NOT NULL,
    remaining_views integer NOT NULL,
    status varchar(20) NOT NULL,
    payment_id varchar NOT NULL,
    foreign key (user_id) references users(id),
    foreign key (event_id) references event(id),
    foreign key (payment_id) references promotion_payment(id)
);