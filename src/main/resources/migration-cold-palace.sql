-- Add the cold-palace status without changing existing VIP records.
ALTER TABLE vip
    ADD COLUMN in_cold_palace BOOLEAN NOT NULL DEFAULT FALSE;
