ALTER TABLE users
    ADD COLUMN IF NOT EXISTS banned boolean
    DEFAULT false;