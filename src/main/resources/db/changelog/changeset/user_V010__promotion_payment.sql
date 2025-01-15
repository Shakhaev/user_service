CREATE TABLE promotion_payment (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    user_id bigint,
    amount numeric,
    status varchar(20) NOT NULL,
    payment_type varchar(20) NOT NULL,
    foreign key (user_id) references users(id)
);