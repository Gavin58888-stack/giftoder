-- 對已存在的 gift_order 資料庫執行一次，將資料庫、資料表及文字欄位轉為 utf8mb4。
ALTER DATABASE gift_order CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE gift_order.vip CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE gift_order.gift_order CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
