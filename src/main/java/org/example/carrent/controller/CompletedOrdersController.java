package org.example.carrent.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import org.example.carrent.db.CompletedOperationDB;
import org.example.carrent.model.Operation;

import java.util.List;

// Контроллер раздела "Завершенные заказы"
public class CompletedOrdersController {

    @FXML
    private TableView<Operation> completedOrdersTable;
    @FXML
    private TableColumn<Operation, String> clientColumn;
    @FXML
    private TableColumn<Operation, String> clientPhoneColumn;
    @FXML
    private TableColumn<Operation, String> clientPassportSeriesColumn;
    @FXML
    private TableColumn<Operation, String> clientPassportNumberColumn;
    @FXML
    private TableColumn<Operation, String> carColumn;
    @FXML
    private TableColumn<Operation, String> carPlateColumn;
    @FXML
    private TableColumn<Operation, String> startDateColumn;
    @FXML
    private TableColumn<Operation, String> endDateColumn;
    @FXML
    private TableColumn<Operation, Number> totalCostColumn;

    // Форматтер для дат
    private java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @FXML
    public void initialize() {
        // Настраиваем фабрики значений для колонок
        clientColumn.setCellValueFactory(new ClientNameCellValueFactory());
        clientPhoneColumn.setCellValueFactory(new ClientPhoneCellValueFactory());
        clientPassportSeriesColumn.setCellValueFactory(new ClientPassportSeriesCellValueFactory());
        clientPassportNumberColumn.setCellValueFactory(new ClientPassportNumberCellValueFactory());
        carColumn.setCellValueFactory(new CarCellValueFactory());
        carPlateColumn.setCellValueFactory(new CarPlateCellValueFactory());
        startDateColumn.setCellValueFactory(new StartDateCellValueFactory());
        endDateColumn.setCellValueFactory(new EndDateCellValueFactory());
        totalCostColumn.setCellValueFactory(new TotalCostCellValueFactory());

        refreshTable();
    }

    // Обновление таблицы
    private void refreshTable() {
        List<Operation> operationList = CompletedOperationDB.getCompletedOperations();
        ObservableList<Operation> observableList = FXCollections.observableArrayList(operationList);
        completedOrdersTable.setItems(observableList);
    }

    // === Фабрики значений для колонок ===

    // Фабрика для имени клиента
    private class ClientNameCellValueFactory implements Callback<TableColumn.CellDataFeatures<Operation, String>, ObservableValue<String>> {
        @Override
        public ObservableValue<String> call(TableColumn.CellDataFeatures<Operation, String> cellData) {
            Operation operation = cellData.getValue();
            return new SimpleStringProperty(operation.getClient().getName());
        }
    }

    // Фабрика для телефона клиента
    private class ClientPhoneCellValueFactory implements Callback<TableColumn.CellDataFeatures<Operation, String>, ObservableValue<String>> {
        @Override
        public ObservableValue<String> call(TableColumn.CellDataFeatures<Operation, String> cellData) {
            Operation operation = cellData.getValue();
            return new SimpleStringProperty(operation.getClient().getPhone());
        }
    }

    // Фабрика для серии паспорта
    private class ClientPassportSeriesCellValueFactory implements Callback<TableColumn.CellDataFeatures<Operation, String>, ObservableValue<String>> {
        @Override
        public ObservableValue<String> call(TableColumn.CellDataFeatures<Operation, String> cellData) {
            Operation operation = cellData.getValue();
            return new SimpleStringProperty(operation.getClient().getPassportSeries());
        }
    }

    // Фабрика для номера паспорта
    private class ClientPassportNumberCellValueFactory implements Callback<TableColumn.CellDataFeatures<Operation, String>, ObservableValue<String>> {
        @Override
        public ObservableValue<String> call(TableColumn.CellDataFeatures<Operation, String> cellData) {
            Operation operation = cellData.getValue();
            return new SimpleStringProperty(operation.getClient().getPassportNumber());
        }
    }

    // Фабрика для автомобиля
    private class CarCellValueFactory implements Callback<TableColumn.CellDataFeatures<Operation, String>, ObservableValue<String>> {
        @Override
        public ObservableValue<String> call(TableColumn.CellDataFeatures<Operation, String> cellData) {
            Operation operation = cellData.getValue();
            String carInfo = operation.getCar().getMake() + " " + operation.getCar().getModel();
            return new SimpleStringProperty(carInfo);
        }
    }

    // Фабрика для номера автомобиля
    private class CarPlateCellValueFactory implements Callback<TableColumn.CellDataFeatures<Operation, String>, ObservableValue<String>> {
        @Override
        public ObservableValue<String> call(TableColumn.CellDataFeatures<Operation, String> cellData) {
            Operation operation = cellData.getValue();
            return new SimpleStringProperty(operation.getCar().getPlate());
        }
    }

    // Фабрика для даты начала
    private class StartDateCellValueFactory implements Callback<TableColumn.CellDataFeatures<Operation, String>, ObservableValue<String>> {
        @Override
        public ObservableValue<String> call(TableColumn.CellDataFeatures<Operation, String> cellData) {
            Operation operation = cellData.getValue();
            return new SimpleStringProperty(operation.getStartDate().format(dateFormatter));
        }
    }

    // Фабрика для даты окончания
    private class EndDateCellValueFactory implements Callback<TableColumn.CellDataFeatures<Operation, String>, ObservableValue<String>> {
        @Override
        public ObservableValue<String> call(TableColumn.CellDataFeatures<Operation, String> cellData) {
            Operation operation = cellData.getValue();
            return new SimpleStringProperty(operation.getEndDate().format(dateFormatter));
        }
    }

    // Фабрика для стоимости
    private class TotalCostCellValueFactory implements Callback<TableColumn.CellDataFeatures<Operation, Number>, ObservableValue<Number>> {
        @Override
        public ObservableValue<Number> call(TableColumn.CellDataFeatures<Operation, Number> cellData) {
            Operation operation = cellData.getValue();
            return new SimpleIntegerProperty(operation.getTotalCost());
        }
    }
}
