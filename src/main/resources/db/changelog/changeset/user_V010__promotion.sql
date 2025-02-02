CREATE TABLE promotion (
    id bigserial PRIMARY KEY,
    user_id bigint NOT NULL,
    event_id bigint,
    tariff varchar(30) NOT NULL,
    plan_type varchar(30) NOT NULL,
    used_views integer NOT NULL,
    status varchar(30) NOT NULL,
    payment_id uuid NOT NULL,
    foreign key (user_id) references users(id),
    foreign key (event_id) references event(id),
    foreign key (payment_id) references promotion_payment(id)
);

CREATE INDEX idx_promotion_user_id ON promotion (user_id);