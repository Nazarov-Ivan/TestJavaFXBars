package com.bars.testjavafx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContractApplication extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContractApplication.class);

    @Override
    public void start(Stage stage) throws IOException {

        Thread thread = new Thread(new Server());
        thread.start();

        FXMLLoader fxmlLoader = new FXMLLoader(ContractApplication.class.getResource("contract-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 410, 564);
        stage.setTitle("Список договоров");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(new EventHandler<>() {
            public void handle(WindowEvent we) {
                Platform.exit();
                thread.interrupt();
                LOGGER.info("Application closed");
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}