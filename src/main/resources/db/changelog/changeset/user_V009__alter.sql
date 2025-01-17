ALTER TABLE users
ADD tariff_id bigint,
ADD CONSTRAINT fk_tariff_id FOREIGN KEY (tariff_id) REFERENCES country (id)
