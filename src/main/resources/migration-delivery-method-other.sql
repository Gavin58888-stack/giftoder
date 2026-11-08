-- Store the free-text detail for VIPs whose delivery method is "其他".
ALTER TABLE vip
    ADD COLUMN delivery_method_other VARCHAR(100) NULL;
