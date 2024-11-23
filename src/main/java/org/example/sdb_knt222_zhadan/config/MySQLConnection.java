package org.example.sdb_knt222_zhadan.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MySQLConnection {
    private static Connection connection;

    private MySQLConnection() {}

    public static synchronized Connection getConnection() {
        if (connection == null) {
            try {
                Properties properties = new Properties();
                try (InputStream input = MySQLConnection.class.getClassLoader().getResourceAsStream("application.properties")) {
                    if (input == null) {
                        throw new RuntimeException("Не вдалося знайти файл конфігурації application.properties");
                    }
                    properties.load(input);
                }

                String url = properties.getProperty("spring.datasource.url");
                String user = properties.getProperty("spring.datasource.username");
                String password = properties.getProperty("spring.datasource.password");

                connection = DriverManager.getConnection(url, user, password);
            } catch (IOException | SQLException e) {
                throw new RuntimeException("Не вдалося підключитися до бази даних", e);
            }
        }
        return connection;
    }
}
