package com.bars.testjavafx;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContractController {

    private ObservableList<Contract> contracts;

    @FXML
    private VBox vboxApp;

    @FXML
    private SplitPane scrollOne;

    @FXML
    private ScrollPane scrollTwo;

    @FXML
    private AnchorPane mainAnchor;

    @FXML
    private Label mainLabel;

    @FXML
    private Font x5;

    @FXML
    private TableView<Contract> listContracts;

    @FXML
    private Button refreshButton;

    @FXML
    private TableColumn<Contract, String> numberContract;

    @FXML
    private TableColumn<Contract, String> date;

    @FXML
    private TableColumn<Contract, String> lastUpdate;

    @FXML
    private TableColumn<Contract, CheckBox> relevance;

    private static final Logger LOGGER = LoggerFactory.getLogger(ContractController.class);

    @FXML
    void initialize() throws InterruptedException {

        contracts = FXCollections.observableArrayList(getContractsFromServer());
        numberContract.setCellValueFactory(new PropertyValueFactory<Contract, String>("number"));
        date.setCellValueFactory(new PropertyValueFactory<Contract, String>("date"));
        lastUpdate.setCellValueFactory(new PropertyValueFactory<Contract, String>("lastUpdate"));
        relevance.setCellValueFactory(new PropertyValueFactory<Contract, CheckBox>("relevance"));

        for (Contract contract : contracts) {
            checkLastUpdate(contract);
        }

        listContracts.setItems(contracts);

        refreshButton.setOnAction(event -> {
            try {
                initialize();
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage());
            }
        });
    }

    private List<Contract> getContractsFromServer() {
        ObjectMapper mapper = new ObjectMapper();
        List<Contract> contracts = new ArrayList<>();

        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/"))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("HttpResponseCode: " + response.statusCode());
            } else {
                String informationString = response.body();
                contracts = Arrays.asList(mapper.readValue(informationString, Contract[].class));
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.error(e.getMessage());
        }

        return contracts;
    }

    private void checkLastUpdate(Contract contract) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate lastUpdateDate = LocalDate.parse(contract.getLastUpdate(), formatter);
        Duration duration = Duration.between(lastUpdateDate.atStartOfDay(), LocalDate.now().atStartOfDay());
        long days = duration.toDays();
        contract.getRelevance().setSelected(days <= 60);
    }
}