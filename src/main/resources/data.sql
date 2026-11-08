-- Ensure MySQL reads this UTF-8 seed file as UTF-8 rather than latin1.
SET NAMES utf8mb4;

INSERT INTO vip (name, title, phone, mobile, address, delivery_method)
VALUES ('張總經理', 'ABC 科技', '02-23456789', '0912-345678', '台北市信義路五段7號', '宅配');

INSERT INTO vip (name, title, phone, mobile, address, delivery_method)
VALUES ('李董事長', 'XYZ 集團', '02-87654321', '0922-987654', '台北市大安區忠孝東路四段100號', '親送');


-- 修正既有資料庫中已存成亂碼的兩筆種子資料。
UPDATE vip
SET name = '張總經理', title = 'ABC 科技', phone = '02-23456789',
    mobile = '0912-345678', address = '台北市信義路五段7號', delivery_method = '宅配'
WHERE phone = '02-23456789';

UPDATE vip
SET name = '李董事長', title = 'XYZ 集團', phone = '02-87654321',
    mobile = '0922-987654', address = '台北市大安區忠孝東路四段100號', delivery_method = '親送'
WHERE phone = '02-87654321';

-- 匯入「專題貴賓資料 - 工作表1.pdf」中的貴賓資料。
INSERT INTO vip (name, title, phone, mobile, address, delivery_method)
VALUES
    ('許光漢 副院長', '台大醫院', '02-22610568', '925776011', '新北市新莊區思源路 127號', NULL),
    ('邱澤 秘書', '許光漢 副院長', NULL, '913571124', '臺北市信義區吳興街252號', NULL),
    ('趙勻廷 科主任', '台積電', '02-22610570', NULL, '臺北市中正區中山南路7號', NULL),
    ('吳慷仁 董事長', '八方雲集', '02-22610571', NULL, '臺北市大同區鄭州路145號', NULL),
    ('謝盈萱 會計師', '長榮會計事務所', NULL, '975885656', '臺中市西區三民路一段199號', NULL),
    ('林柏宏 先生', '富邦金控', '02-22610573', '975885752', '臺中市北區育德路2號', NULL),
    ('瑞莎 律師', '王品律師事務所', NULL, '975885858', '高雄市三民區自由一路100號', NULL),
    ('李英愛 顧問', NULL, '02-22610575', NULL, '高雄市鳥松區大埤路123號', NULL),
    ('Elon Musk 董事長', 'San Bernardino National Forest', '+1 909-382-2600', NULL, '40500 Paseo Padre Pkwy, Fremont, CA 94538美國', NULL);
