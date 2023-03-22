package com.bars.testjavafx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBWorker {

    private Connection connection;
    private static final Logger LOGGER = LoggerFactory.getLogger(ContractController.class);

    public DBWorker() throws SQLException {
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(Paths.get("src/database.properties"))) {
            props.load(in);
            connection = DriverManager.getConnection(props.getProperty("HOST"),
                    props.getProperty("USERNAME"), props.getProperty("PASSWORD"));
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }
}