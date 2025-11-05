-- Add cancelled_at column expected by JPA entity
ALTER TABLE booking ADD COLUMN IF NOT EXISTS cancelled_at TIMESTAMP;

