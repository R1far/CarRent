package org.example.carrent.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Класс для подключения к базе данных SQLite
public class Database {

    // Путь к файлу базы данных
    private static final String DB_URL = "jdbc:sqlite:carrent.db";

    // Получить соединение с базой данных
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
