CREATE TABLE promotion_payment (
    id uuid PRIMARY KEY UNIQUE,
    user_id bigint,
    amount numeric,
    currency varchar(10),
    status varchar(20) NOT NULL,
    foreign key (user_id) references users(id)
);