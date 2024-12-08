CREATE TABLE IF NOT EXISTS user_profile (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    preference INT NOT NULL DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_user_id ON user_profile(user_id);

INSERT INTO user_profile (user_id, preference)
SELECT id, 0
FROM users
WHERE NOT EXISTS (
    SELECT 1
    FROM user_profile
    WHERE user_profile.user_id = users.id
);