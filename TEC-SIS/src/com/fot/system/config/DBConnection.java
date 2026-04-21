package com.fot.system.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConnection {
    private static final String[] PROPERTY_LOCATIONS = {
            "db.properties",
            "db.propertise",
            "src/db.properties",
            "src/db.propertise",
            "TEC-SIS/src/db.properties",
            "TEC-SIS/src/db.propertise"
    };

    private static DBConnection instance;
    private Connection connection;

    private String URL;
    private String USER;
    private String PASSWORD;

    private DBConnection() {
        try {
            Properties props = new Properties();
            try (InputStream input = openPropertiesStream()) {
                props.load(input);
            }

            URL = props.getProperty("db.url");
            USER = props.getProperty("db.user");
            PASSWORD = props.getProperty("db.password");

            // Load driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create connection
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private InputStream openPropertiesStream() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();

        for (String location : PROPERTY_LOCATIONS) {
            InputStream resourceStream = classLoader.getResourceAsStream(location);
            if (resourceStream != null) {
                return resourceStream;
            }
        }

        for (String location : PROPERTY_LOCATIONS) {
            Path path = Path.of(location);
            if (Files.exists(path)) {
                return Files.newInputStream(path);
            }
        }

        throw new IllegalStateException("Database properties file not found.");
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null) {
                    instance = new DBConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
