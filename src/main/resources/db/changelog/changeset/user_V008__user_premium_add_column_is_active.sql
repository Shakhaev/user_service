ALTER TABLE user_premium
    ADD COLUMN if not exists is_active boolean NOT NULL DEFAULT true;