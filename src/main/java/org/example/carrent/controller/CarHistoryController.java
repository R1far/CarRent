package org.example.carrent.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.carrent.db.OperationDB;
import org.example.carrent.model.Car;
import org.example.carrent.model.Operation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Контроллер окна истории бронирований автомобиля
public class CarHistoryController {

    @FXML
    private Label carTitleLabel;
    @FXML
    private Label carPlateLabel;
    @FXML
    private VBox historyContainer;
    @FXML
    private DatePicker filterStart;
    @FXML
    private DatePicker filterEnd;
    @FXML
    private Button closeButton;

    // Текущий автомобиль
    private Car currentCar;
    // Все операции для этого автомобиля
    private List<Operation> allOperations;

    @FXML
    public void initialize() {
        // Запрещаем ручной ввод дат
        filterStart.setEditable(false);
        filterEnd.setEditable(false);
    }

    // Установка автомобиля для просмотра истории
    public void setCar(Car car) {
        this.currentCar = car;
        carTitleLabel.setText(car.getMake() + " " + car.getModel());
        carPlateLabel.setText(car.getPlate());

        // Загружаем все операции для автомобиля
        this.allOperations = OperationDB.getOperations(car.getId());

        renderList(this.allOperations);
    }

    // Применение фильтра по датам
    @FXML
    public void applyFilter() {
        if (allOperations == null) {
            return;
        }

        LocalDate startFilter = filterStart.getValue();
        LocalDate endFilter = filterEnd.getValue();

        // Фильтруем список операций
        List<Operation> filteredList = new ArrayList<>();

        for (Operation operation : allOperations) {
            boolean include = true;

            // Проверяем фильтр по дате начала
            if (startFilter != null) {
                if (operation.getEndDate().isBefore(startFilter)) {
                    include = false;
                }
            }

            // Проверяем фильтр по дате окончания
            if (endFilter != null) {
                if (operation.getStartDate().isAfter(endFilter)) {
                    include = false;
                }
            }

            if (include) {
                filteredList.add(operation);
            }
        }

        renderList(filteredList);
    }

    // Отрисовка списка операций
    private void renderList(List<Operation> operationList) {
        historyContainer.getChildren().clear();

        // Если список пустой - показываем сообщение
        if (operationList.isEmpty()) {
            Label emptyLabel = new Label("Нет бронирований");
            emptyLabel.setStyle("-fx-text-fill: grey; -fx-padding: 20;");
            historyContainer.getChildren().add(emptyLabel);
            return;
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        // Создаём строку для каждой операции
        for (Operation operation : operationList) {
            HBox row = new HBox();
            row.setPadding(new Insets(10));
            row.setSpacing(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-border-color: #e2e8f0; -fx-border-radius: 5; -fx-background-color: white;");

            // Блок с датами и именем клиента
            VBox datesBox = new VBox();
            String dateRange = operation.getStartDate().format(dateFormatter) + " — " +
                              operation.getEndDate().format(dateFormatter);
            Label datesLabel = new Label(dateRange);
            datesLabel.setStyle("-fx-font-weight: bold; -fx-font-family: 'Monospaced'; -fx-font-size: 12px;");

            Label clientNameLabel = new Label(operation.getClient().getName());
            datesBox.getChildren().add(datesLabel);
            datesBox.getChildren().add(clientNameLabel);

            // Спейсер
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // Бейдж статуса
            Label statusLabel = new Label(operation.getStatus());

            // Стилизуем бейдж в зависимости от статуса
            if ("active".equals(operation.getStatus())) {
                statusLabel.setStyle("-fx-background-color: #dbeafe; -fx-text-fill: #1e40af; " +
                                    "-fx-padding: 2 6; -fx-background-radius: 4; " +
                                    "-fx-font-weight: bold; -fx-font-size: 10px;");
                statusLabel.setText("ТЕКУЩАЯ");
            } else if ("completed".equals(operation.getStatus())) {
                statusLabel.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #64748b; " +
                                    "-fx-padding: 2 6; -fx-background-radius: 4; " +
                                    "-fx-font-weight: bold; -fx-font-size: 10px;");
                statusLabel.setText("ЗАВЕРШЕНА");
            } else {
                statusLabel.setStyle("-fx-background-color: #fef3c7; -fx-text-fill: #92400e; " +
                                    "-fx-padding: 2 6; -fx-background-radius: 4; " +
                                    "-fx-font-weight: bold; -fx-font-size: 10px;");
                statusLabel.setText("БРОНЬ");
            }

            row.getChildren().add(datesBox);
            row.getChildren().add(spacer);
            row.getChildren().add(statusLabel);

            historyContainer.getChildren().add(row);
        }
    }

    // Закрытие окна
    @FXML
    private void onClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
