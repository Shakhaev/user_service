ALTER TABLE user_premium
DROP COLUMN banned;

ALTER TABLE users
ADD COLUMN banned BOOLEAN NOT NULL DEFAULT FALSE;