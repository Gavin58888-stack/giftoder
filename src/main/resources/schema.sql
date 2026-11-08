-- 統一資料庫預設字元集與排序規則。
ALTER DATABASE gift_order CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- 1. 貴賓名單主檔（擴充地址、電話、手機、配送方式）
CREATE TABLE IF NOT EXISTS vip (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,        -- 貴賓姓名
    title VARCHAR(100),                -- 職稱/單位
    phone VARCHAR(50),                 -- 電話
    mobile VARCHAR(50),                -- 手機
    address VARCHAR(255),              -- 地址
    notes VARCHAR(500),                 -- 備註
    delivery_method VARCHAR(50),       -- 配送方式 (例如：親送、宅配、郵寄、其他)
    delivery_method_other VARCHAR(100),
    in_cold_palace BOOLEAN NOT NULL DEFAULT FALSE -- 是否在冷宮名單
) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- 2. 送禮紀錄表
CREATE TABLE IF NOT EXISTS gift_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vip_id BIGINT NOT NULL,
    gift_item VARCHAR(100) DEFAULT '蘋果禮盒',
    gift_item_2 VARCHAR(100),
    gift_item_3 VARCHAR(100),
    is_selected BOOLEAN DEFAULT FALSE,
    quantity INT DEFAULT 0,
    quantity_2 INT DEFAULT 0,
    quantity_3 INT DEFAULT 0
) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- CREATE TABLE IF NOT EXISTS 不會變更既有資料表，因此明確轉換既有欄位。
ALTER TABLE vip CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE gift_order CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
