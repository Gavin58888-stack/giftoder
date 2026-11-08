-- Add a notes field to existing VIP records without changing their data.
ALTER TABLE vip
    ADD COLUMN notes VARCHAR(500) NULL;
