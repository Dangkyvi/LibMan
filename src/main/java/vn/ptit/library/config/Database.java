package vn.ptit.library.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** JDBC configuration only. Opening the application never creates tables or imports data. */
public final class Database {
    private static final Properties CONFIG = loadConfig();

    private Database() {}

    private static Properties loadConfig() {
        Properties config = new Properties();
        try (InputStream input = Database.class.getResourceAsStream("/database.properties")) {
            if (input == null) {
                throw new IllegalStateException("Không tìm thấy tệp database.properties.");
            }
            config.load(input);
            return config;
        } catch (IOException e) {
            throw new IllegalStateException("Không thể đọc cấu hình cơ sở dữ liệu.", e);
        }
    }

    private static String setting(String key) {
        String property = System.getProperty(key);
        if (property != null) return property;
        String environment = System.getenv(key);
        return environment != null ? environment : CONFIG.getProperty(key, "");
    }

    public static Connection open() throws SQLException {
        String url = setting("DB_URL");
        // Tomcat may initialize DriverManager before WEB-INF/lib is visible.
        // Explicit loading registers Connector/J with this web application's classloader.
        if (url.startsWith("jdbc:mysql:")) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Không tìm thấy MySQL JDBC driver trong ứng dụng.", e);
            }
        }
        return DriverManager.getConnection(url, setting("DB_USER"), setting("DB_PASSWORD"));
    }
}
