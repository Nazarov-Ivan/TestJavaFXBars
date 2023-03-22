package com.bars.testjavafx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Server implements Runnable {
    private final static int BUFFER_SIZE = 256;
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private final static String HEADERS =
            "HTTP/1.1 200 OK\n"
                    + "Server: naive\n"
                    + "Content-Type: text/html\n"
                    + "Content-Length: %s\n"
                    + "Connection: close\n\n";

    @Override
    public void run() {
        try (AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open()) {
            server.bind(new InetSocketAddress("127.0.0.1", 8080));

            while (true) {
                Future<AsynchronousSocketChannel> future = server.accept();
                handleClient(future);
            }
        } catch (IOException | InterruptedException | ExecutionException | TimeoutException e) {
            LOGGER.info("Server stopped");
        }
    }

    private void handleClient(Future<AsynchronousSocketChannel> future)
            throws InterruptedException, ExecutionException, TimeoutException, IOException {

        AsynchronousSocketChannel clientChannel = future.get(1, TimeUnit.HOURS);

        while (clientChannel != null && clientChannel.isOpen()) {
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            StringBuilder builder = new StringBuilder();
            boolean keepReading = true;

            while (keepReading) {
                clientChannel.read(buffer).get();

                int position = buffer.position();
                keepReading = position == BUFFER_SIZE;

                byte[] array = keepReading
                        ? buffer.array()
                        : Arrays.copyOfRange(buffer.array(), 0, position);

                builder.append(new String(array, StandardCharsets.UTF_8));
                buffer.clear();
            }

            String body;
            try {
                body = getJsonFromDB();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            if (body != null) {
                String page = String.format(HEADERS, body.length()) + body;
                ByteBuffer resp = ByteBuffer.wrap(page.getBytes(StandardCharsets.UTF_8));
                clientChannel.write(resp);
            }
            clientChannel.close();
        }
    }

    public String getJsonFromDB() throws IOException, SQLException {

        String query = "select * from contracts";
        List<Contract> contracts = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String json = null;
        DBWorker worker = new DBWorker();
        try (
             Statement statement = worker.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            createAndFillingTable(worker);
            while (resultSet.next()) {
                Contract contract = new Contract();
                contract.setId(resultSet.getInt(1));
                contract.setNumber(resultSet.getInt(2));
                contract.setDate(resultSet.getDate(3));
                contract.setLastUpdate(resultSet.getDate(4));
                contracts.add(contract);
            }
            json = mapper.writeValueAsString(contracts);

        } catch (JsonProcessingException e) {
            LOGGER.error("Error while getting data from database: " + e.getMessage());
        }
        return json;

    }

    private void createAndFillingTable(DBWorker worker) {
        try {
            DatabaseMetaData md = worker.getConnection().getMetaData();
            ResultSet rs = md.getTables(null, null, "contracts", null);
            if (!rs.next()) {
                createContractsTable(worker);
                insertTestData(worker);
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void createContractsTable(DBWorker worker) throws SQLException {
        try (Statement statement = worker.getConnection().createStatement()) {
            String sql = "CREATE TABLE contracts (\n"
                    + "id BIGSERIAL NOT NULL PRIMARY KEY,\n"
                    + "number INTEGER NOT NULL,\n"
                    + "date DATE NOT NULL,\n"
                    + "last_update DATE NOT NULL\n"
                    + ")";
            statement.execute(sql);
        }
    }

    private void insertTestData(DBWorker worker) throws SQLException {
        try (PreparedStatement statement = worker.getConnection().prepareStatement(
                "INSERT INTO contracts (id, number, date, last_update) VALUES (?, ?, ?, ?)")) {
            statement.setInt(1, 1);
            statement.setInt(2, 1);
            statement.setDate(3, Date.valueOf("1994-05-12"));
            statement.setDate(4, Date.valueOf("2023-01-12"));
            statement.executeUpdate();

            statement.setInt(1, 2);
            statement.setInt(2, 2);
            statement.setDate(3, Date.valueOf("2002-06-04"));
            statement.setDate(4, Date.valueOf("2022-12-01"));
            statement.executeUpdate();

            statement.setInt(1, 3);
            statement.setInt(2, 3);
            statement.setDate(3, Date.valueOf("2007-11-16"));
            statement.setDate(4, Date.valueOf("2023-03-09"));
            statement.executeUpdate();

            statement.setInt(1, 4);
            statement.setInt(2, 4);
            statement.setDate(3, Date.valueOf("2014-06-13"));
            statement.setDate(4, Date.valueOf("2023-02-22"));
            statement.executeUpdate();

            statement.setInt(1, 5);
            statement.setInt(2, 5);
            statement.setDate(3, Date.valueOf("2018-08-10"));
            statement.setDate(4, Date.valueOf("2021-06-09"));
            statement.executeUpdate();
        }
    }
}