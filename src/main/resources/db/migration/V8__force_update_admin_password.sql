-- Force update lead accounts with correct bcrypt hash for 'password123'
-- Bcrypt Hash: $2a$10$uYxZtKxy0scNyAblaHSxpe6u2zpw46raAJr/DNBZzrM4/1XpvHBe2

UPDATE users 
SET password = '$2a$10$uYxZtKxy0scNyAblaHSxpe6u2zpw46raAJr/DNBZzrM4/1XpvHBe2',
    is_locked = false,
    failed_login_attempts = 0,
    is_active = true
WHERE dsa_unique_code IN ('ADMIN001', 'CHECKER001', 'DSA_ACTIVE_001');
