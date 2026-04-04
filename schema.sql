CREATE DATABASE IF NOT EXISTS fuel_cons CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE fuel_cons;

CREATE TABLE IF NOT EXISTS calculator_mem (
    id INT AUTO_INCREMENT PRIMARY KEY,
    distance DOUBLE NOT NULL,
    fuel DOUBLE NOT NULL,
    price DOUBLE NOT NULL,
    total_fuel DOUBLE NOT NULL,
    total_cost DOUBLE NOT NULL,
    language VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS localization_strings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    `key` VARCHAR(100) NOT NULL,
    value VARCHAR(255) NOT NULL,
    language VARCHAR(10) NOT NULL,
    UNIQUE KEY unique_key_lang (`key`, `language`)
);

INSERT INTO localization_strings (`key`, `value`, `language`) VALUES
-- English
('title', 'Consumption Calculator', 'en'),
('lblDistance', 'Distance (km)', 'en'),
('lblFuel', 'Fuel Consumption (L/100 km)', 'en'),
('lblPrice', 'Fuel price per liter', 'en'),
('distance', 'Trip distance in kilometers', 'en'),
('fuel', 'Fuel consumption (L/100 km)', 'en'),
('price', 'Fuel price per liter', 'en'),
('calculate', 'Calculate', 'en'),
('calc_result', 'Total fuel needed: %.2f L | Total cost: %.2f', 'en'),
('error_invalid_input', 'Please enter valid numbers', 'en'),
-- Persian (fa)
('title', 'محاسبه‌گر مصرف سوخت', 'fa'),
('lblDistance', 'مسافت (کیلومتر)', 'fa'),
('lblFuel', 'مصرف سوخت (لیتر در ۱۰۰ کیلومتر)', 'fa'),
('lblPrice', 'قیمت سوخت هر لیتر', 'fa'),
('distance', 'مسافت سفر به کیلومتر', 'fa'),
('fuel', 'مصرف سوخت (لیتر در ۱۰۰ کیلومتر)', 'fa'),
('price', 'قیمت سوخت هر لیتر', 'fa'),
('calculate', 'محاسبه', 'fa'),
('calc_result', 'کل سوخت مورد نیاز: %.2f لیتر | کل هزینه: %.2f', 'fa'),
('error_invalid_input', 'لطفاً اعداد معتبر وارد کنید', 'fa'),
-- French (fr)
('title', 'Calculateur de consommation', 'fr'),
('lblDistance', 'Distance (km)', 'fr'),
('lblFuel', 'Consommation de carburant (L/100 km)', 'fr'),
('lblPrice', 'Prix du carburant par litre', 'fr'),
('distance', 'Distance du trajet en kilomètres', 'fr'),
('fuel', 'Consommation de carburant (L/100 km)', 'fr'),
('price', 'Prix du carburant par litre', 'fr'),
('calculate', 'Calculer', 'fr'),
('calc_result', 'Carburant total nécessaire : %.2f L | Coût total : %.2f', 'fr'),
('error_invalid_input', 'Veuillez entrer des nombres valides', 'fr'),
-- Japanese (ja)
('title', '消費量計算機', 'ja'),
('lblDistance', '距離 (km)', 'ja'),
('lblFuel', '燃料消費量 (L/100 km)', 'ja'),
('lblPrice', '燃料価格（1リットルあたり）', 'ja'),
('distance', '移動距離（km）', 'ja'),
('fuel', '燃料消費量 (L/100 km)', 'ja'),
('price', '燃料価格（1リットルあたり）', 'ja'),
('calculate', '計算', 'ja'),
('calc_result', '必要な燃料合計: %.2f L | 合計コスト: %.2f', 'ja'),
('error_invalid_input', '有効な数字を入力してください', 'ja');