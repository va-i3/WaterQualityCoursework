package com.water.client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class WaterClientApp extends Application {

    private static final Logger LOG = Logger.getLogger(WaterClientApp.class.getName());

    private final WaterHttpClient client = new WaterHttpClient("http://localhost:8080/water-monitoring-server");
    private final CsvMeasurementImporter csvImporter = new CsvMeasurementImporter(client);
    private final TableView<Measurement> table = new TableView<>();
    private final ProgressBar importProgressBar = new ProgressBar(0);
    private final Label statusLabel = new Label("Готово");

    private Button importCsvBtn;
    private Integer selectedId = null;

    @Override
    public void start(Stage stage) {
        TableColumn<Measurement, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Measurement, Integer> sampleCol = new TableColumn<>("Sample ID");
        sampleCol.setCellValueFactory(new PropertyValueFactory<>("waterSampleId"));

        TableColumn<Measurement, Double> p1Col = new TableColumn<>("Param 1");
        p1Col.setCellValueFactory(new PropertyValueFactory<>("parameter1"));

        TableColumn<Measurement, Double> p2Col = new TableColumn<>("Param 2");
        p2Col.setCellValueFactory(new PropertyValueFactory<>("parameter2"));

        TableColumn<Measurement, Double> p3Col = new TableColumn<>("Param 3");
        p3Col.setCellValueFactory(new PropertyValueFactory<>("parameter3"));

        TableColumn<Measurement, Double> wqiCol = new TableColumn<>("WQI");
        wqiCol.setCellValueFactory(new PropertyValueFactory<>("waterQualityIndex"));

        @SuppressWarnings("unchecked")
        TableColumn<Measurement, ?>[] columns = new TableColumn[]{idCol, sampleCol, p1Col, p2Col, p3Col, wqiCol};
        table.getColumns().addAll(columns);

        TextField sampleIdField = new TextField();
        sampleIdField.setPromptText("Sample ID (напр. 3)");

        TextField p1Field = new TextField();
        p1Field.setPromptText("Param 1 (напр. 80.0)");

        TextField p2Field = new TextField();
        p2Field.setPromptText("Param 2 (напр. 0.0)");

        TextField p3Field = new TextField();
        p3Field.setPromptText("Param 3 (напр. 0.0)");

        TextField wqiField = new TextField();
        wqiField.setPromptText("WQI (напр. 0.0)");

        table.getSelectionModel().selectedItemProperty().addListener((_, _, selected) -> {
            if (selected != null) {
                selectedId = selected.getId();
                sampleIdField.setText(String.valueOf(selected.getWaterSampleId()));
                p1Field.setText(String.valueOf(selected.getParameter1()));
                p2Field.setText(String.valueOf(selected.getParameter2()));
                p3Field.setText(String.valueOf(selected.getParameter3()));
                wqiField.setText(String.valueOf(selected.getWaterQualityIndex()));
            }
        });

        Button addBtn = new Button("Додати");
        addBtn.setOnAction(_ -> {
            try {
                Measurement m = buildMeasurementFromFields(null, sampleIdField, p1Field, p2Field, p3Field, wqiField);
                client.addMeasurement(m);
                statusLabel.setText("Запис додано");
                clearForm(sampleIdField, p1Field, p2Field, p3Field, wqiField);
                loadData();
            } catch (Exception ex) {
                statusLabel.setText("Помилка: " + ex.getMessage());
            }
        });

        Button updateBtn = new Button("Оновити");
        updateBtn.setOnAction(_ -> {
            try {
                Measurement selected = table.getSelectionModel().getSelectedItem();
                int targetId = (selected != null) ? selected.getId() : (selectedId != null ? selectedId : 0);
                if (targetId <= 0) {
                    statusLabel.setText("Оберіть запис у таблиці для оновлення");
                    return;
                }
                Measurement m = buildMeasurementFromFields(targetId, sampleIdField, p1Field, p2Field, p3Field, wqiField);
                client.updateMeasurement(m);
                statusLabel.setText("Запис ID=" + targetId + " оновлено");
                clearForm(sampleIdField, p1Field, p2Field, p3Field, wqiField);
                loadData();
            } catch (Exception ex) {
                statusLabel.setText("Помилка: " + ex.getMessage());
            }
        });

        Button deleteBtn = new Button("Видалити");
        deleteBtn.setOnAction(_ -> {
            try {
                Measurement selected = table.getSelectionModel().getSelectedItem();
                int targetId = (selected != null) ? selected.getId() : (selectedId != null ? selectedId : 0);
                if (targetId <= 0) {
                    statusLabel.setText("Оберіть запис у таблиці для видалення");
                    return;
                }
                client.deleteMeasurement(targetId);
                statusLabel.setText("Запис ID=" + targetId + " видалено");
                clearForm(sampleIdField, p1Field, p2Field, p3Field, wqiField);
                loadData();
            } catch (Exception ex) {
                statusLabel.setText("Помилка: " + ex.getMessage());
            }
        });

        Button reloadBtn = new Button("Оновити дані");
        reloadBtn.setOnAction(_ -> loadData());

        importCsvBtn = new Button("Імпорт CSV");
        importCsvBtn.setOnAction(_ -> startBackgroundCsvImport());

        importProgressBar.setPrefWidth(220);

        HBox formRow1 = new HBox(8, sampleIdField, p1Field, p2Field, p3Field, wqiField);
        HBox buttonsRow = new HBox(8, addBtn, updateBtn, deleteBtn, reloadBtn, importCsvBtn);
        HBox progressRow = new HBox(10, importProgressBar, statusLabel);

        VBox root = new VBox(10, formRow1, buttonsRow, table, progressRow);
        root.setPadding(new Insets(10));

        stage.setScene(new Scene(root, 780, 500));
        stage.setTitle("Water Quality Client");
        stage.show();

        loadData();
    }

    private Measurement buildMeasurementFromFields(
            Integer id,
            TextField sampleIdField,
            TextField p1Field,
            TextField p2Field,
            TextField p3Field,
            TextField wqiField
    ) {
        Measurement m = new Measurement();
        if (id != null) {
            m.setId(id);
        }
        m.setWaterSampleId(Integer.parseInt(sampleIdField.getText().trim()));
        m.setParameter1(Double.parseDouble(p1Field.getText().trim()));
        m.setParameter2(Double.parseDouble(p2Field.getText().trim()));
        m.setParameter3(Double.parseDouble(p3Field.getText().trim()));
        m.setWaterQualityIndex(Double.parseDouble(wqiField.getText().trim()));
        return m;
    }

    private void clearForm(
            TextField sampleIdField,
            TextField p1Field,
            TextField p2Field,
            TextField p3Field,
            TextField wqiField
    ) {
        selectedId = null;
        table.getSelectionModel().clearSelection();
        sampleIdField.clear();
        p1Field.clear();
        p2Field.clear();
        p3Field.clear();
        wqiField.clear();
    }

    private void startBackgroundCsvImport() {
        Task<Integer> task = csvImporter.createImportTask();

        importCsvBtn.setDisable(true);
        importProgressBar.progressProperty().unbind();
        statusLabel.textProperty().unbind();

        importProgressBar.progressProperty().bind(task.progressProperty());
        statusLabel.textProperty().bind(task.messageProperty());

        task.setOnSucceeded(_ -> {
            importProgressBar.progressProperty().unbind();
            statusLabel.textProperty().unbind();
            importCsvBtn.setDisable(false);

            int imported = task.getValue() != null ? task.getValue() : 0;
            importProgressBar.setProgress(1.0);
            statusLabel.setText("CSV-імпорт завершено. Імпортовано записів: " + imported);
            loadData();
        });

        task.setOnFailed(_ -> {
            importProgressBar.progressProperty().unbind();
            statusLabel.textProperty().unbind();
            importCsvBtn.setDisable(false);

            Throwable ex = task.getException();
            String reason = (ex != null && ex.getMessage() != null) ? ex.getMessage() : "Невідома помилка";
            LOG.warning("Фоновий CSV-імпорт завершився помилкою: " + reason);
            importProgressBar.setProgress(0);
            statusLabel.setText("Помилка CSV-імпорту: " + reason);
        });

        Thread worker = new Thread(task, "csv-import-thread");
        worker.setDaemon(true);
        worker.start();
    }

    private void loadData() {
        try {
            var items = client.getMeasurements();
            table.setItems(FXCollections.observableArrayList(items));
            statusLabel.setText("Завантажено записів: " + items.size());
        } catch (Exception ex) {
            statusLabel.setText("Помилка: " + ex.getMessage());
        }
    }

    static void main(String[] args) {
        launch(args);
    }
}