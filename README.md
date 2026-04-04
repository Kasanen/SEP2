# Fuel Consumption Calculator

JavaFX app for calculating total fuel usage and trip cost, with multilingual UI and MariaDB-backed localization/storage.

## Prerequisites

- Linux
- JDK **21**
- Maven 3.9+
- MariaDB server

## 1) Clone and enter project

```bash
git clone <your-repo-url>
cd SEP2
```

## 2) Start MariaDB

```bash
sudo systemctl start mariadb
sudo systemctl enable mariadb
sudo systemctl status mariadb
```

> On some distros, service name is `mysql`.

## 3) Database setup (clear step-by-step)

### Option A (recommended): run full setup script

This project already includes [schema.sql](schema.sql), which creates the DB, tables, and localization seed rows.

Run:

```bash
mariadb -u root -p < schema.sql
```

This creates:

- `fuel_cons` database
- `calculator_mem` table
- `localization_strings` table
- seed localization rows (`en`, `fr`, `ja`, `fa`)

### Verify setup

```sql
USE fuel_cons;
SHOW TABLES;
SELECT language, COUNT(*) FROM localization_strings GROUP BY language;
```

Expected localization result: `10` rows per language.

### Option B (manual setup)

If you do not want to run the full script, run these minimum commands:

```sql
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
```

## 4) Database connection configuration

The app reads DB configuration from `.env` (via `dotenv-java`) with environment variable fallback.

Required keys:

- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`

`DB_URL` format requirements:

- Must use MariaDB JDBC format: `jdbc:mariadb://<host>:<port>/<database>`
- Example: `jdbc:mariadb://localhost:3306/fuel_cons`
- Database name must match the created DB (`fuel_cons`)

Create a `.env` file in project root:

```env
DB_URL=jdbc:mariadb://localhost:3306/fuel_cons
DB_USER=root
DB_PASSWORD=your_password
```

Rules:

- No spaces around `=`
- Keep `.env` in project root
- Ensure MariaDB server is running before app start

## 5) Optional sample data insertion script (recommended)

If you want to quickly test DB reads/writes, insert sample rows:

```sql
USE fuel_cons;

INSERT INTO calculator_mem
  (distance, fuel, price, total_fuel, total_cost, language)
VALUES
  (120.0, 6.5, 1.85, 7.8, 14.43, 'en'),
  (250.0, 7.2, 1.90, 18.0, 34.20, 'fr');

INSERT INTO localization_strings (`key`, `value`, `language`) VALUES
  ('title', 'Consumption Calculator', 'en'),
  ('calculate', 'Calculate', 'en')
ON DUPLICATE KEY UPDATE value = VALUES(value);
```

## 6) Build and run

```bash
mvn clean javafx:run
```

## 7) Run tests

```bash
mvn test
```

## Troubleshooting

### `No suitable driver found for jdbc:mariadb://...`

- Ensure dependency exists in `pom.xml`:
  - `org.mariadb.jdbc:mariadb-java-client`
- Rebuild:

```bash
mvn clean install
```

### `No suitable driver found for null?user=null&password=null`

- `.env` was not loaded or values are missing.
- Check file path and keys: `DB_URL`, `DB_USER`, `DB_PASSWORD`.

### App loads too few localization rows

Check DB content:

```sql
SELECT language, COUNT(*) FROM localization_strings GROUP BY language;
```

Expected: 10 rows per language.

## Notes

- Main entrypoint class: `org.example.fuelConsCalc.FuelConsumptionApp`
- JavaFX UI uses localized keys from DB with fallback to resource bundles.
