package org.example.fuelConsCalc.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;

public class MariaDbConnection {

    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    private static final String URL = dotenv.get("DB_URL", System.getenv("DB_URL"));
    private static final String USER = dotenv.get("DB_USER", System.getenv("DB_USER"));
    private static final String PASSWORD = dotenv.get("DB_PASSWORD", System.getenv("DB_PASSWORD"));

    public static Connection getConnection() throws SQLException {

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

}