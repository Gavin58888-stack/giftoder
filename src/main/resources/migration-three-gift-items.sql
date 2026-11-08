-- Run once on existing installations to support three shared gift items per VIP.
ALTER TABLE gift_order
    ADD COLUMN gift_item_2 VARCHAR(100) NULL,
    ADD COLUMN gift_item_3 VARCHAR(100) NULL,
    ADD COLUMN quantity_2 INT NOT NULL DEFAULT 0,
    ADD COLUMN quantity_3 INT NOT NULL DEFAULT 0;

-- Existing first gift quantities remain intact; new item quantities start at zero.
