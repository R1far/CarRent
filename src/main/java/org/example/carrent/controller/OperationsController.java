package org.example.carrent.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.carrent.db.CarDB;
import org.example.carrent.db.CompletedOperationDB;
import org.example.carrent.db.OperationDB;
import org.example.carrent.model.Operation;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

// Контроллер раздела "Журнал операций" - отображает таблицу аренд
public class OperationsController {

    @FXML
    private TableView<Operation> operationsTable;
    @FXML
    private TableColumn<Operation, String> startDateColumn;
    @FXML
    private TableColumn<Operation, String> endDateColumn;
    @FXML
    private TableColumn<Operation, String> carColumn;
    @FXML
    private TableColumn<Operation, String> clientColumn;
    @FXML
    private TableColumn<Operation, Number> totalCostColumn;
    @FXML
    private TableColumn<Operation, String> statusColumn;
    @FXML
    private TableColumn<Operation, Void> actionsColumn;

    // Поля фильтрации
    @FXML
    private javafx.scene.control.ComboBox<String> statusFilterCombo;
    @FXML
    private javafx.scene.control.DatePicker dateFromPicker;
    @FXML
    private javafx.scene.control.DatePicker dateToPicker;

    // Форматтер для дат
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @FXML
    public void initialize() {
        // Настраиваем фабрики значений для колонок
        startDateColumn.setCellValueFactory(new StartDateCellValueFactory());
        endDateColumn.setCellValueFactory(new EndDateCellValueFactory());
        carColumn.setCellValueFactory(new CarCellValueFactory());
        clientColumn.setCellValueFactory(new ClientCellValueFactory());
        totalCostColumn.setCellValueFactory(new TotalCostCellValueFactory());

        // Настраиваем фабрики ячеек для статуса и действий
        statusColumn.setCellFactory(new StatusCellFactory());
        actionsColumn.setCellFactory(new ActionsCellFactory());

        // Инициализация фильтров
        statusFilterCombo.setItems(FXCollections.observableArrayList("Все", "Аренда", "Бронь"));
        statusFilterCombo.setValue("Все");

        // Запрещаем ручной ввод дат и выбор прошедших дат
        dateFromPicker.setEditable(false);
        dateToPicker.setEditable(false);
        dateFromPicker.setDayCellFactory(new PastDateDisabler());
        dateToPicker.setDayCellFactory(new PastDateDisabler());

        refreshTable();
    }

    // Фабрика ячеек для блокировки прошедших дат
    private class PastDateDisabler implements Callback<javafx.scene.control.DatePicker, javafx.scene.control.DateCell> {
        @Override
        public javafx.scene.control.DateCell call(javafx.scene.control.DatePicker picker) {
            return new javafx.scene.control.DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    // Блокируем даты раньше сегодняшней
                    if (empty || date.isBefore(LocalDate.now())) {
                        setDisable(true);
                    }
                }
            };
        }
    }

    // Обработчик кнопки фильтрации
    @FXML
    protected void onFilterClick() {
        refreshTable();
    }

    // Обработчик сброса фильтра
    @FXML
    protected void onResetFilterClick() {
        statusFilterCombo.setValue("Все");
        dateFromPicker.setValue(null);
        dateToPicker.setValue(null);
        refreshTable();
    }

    // Обновление таблицы операций
    private void refreshTable() {
        String selectedStatus = statusFilterCombo.getValue();
        String dbStatus = null;

        // Маппинг русских названий в английские статусы БД
        if ("Аренда".equals(selectedStatus)) {
            dbStatus = "active";
        } else if ("Бронь".equals(selectedStatus)) {
            dbStatus = "pending";
        }
        // "Все" -> null

        LocalDate dateFrom = dateFromPicker.getValue();
        LocalDate dateTo = dateToPicker.getValue();

        // Запрашиваем операции, исключая завершенные (false)
        List<Operation> operationList = OperationDB.getFilteredOperations(dbStatus, dateFrom, dateTo, false);
        ObservableList<Operation> observableList = FXCollections.observableArrayList(operationList);
        operationsTable.setItems(observableList);
    }

    // Обработчик кнопки добавления операции
    @FXML
    protected void onAddOperationClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/carrent/operation-form-view.fxml"));
            Parent parent = fxmlLoader.load();

            Scene scene = new Scene(parent, 400, 400);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Новая запись");
            stage.setScene(scene);
            stage.showAndWait();

            // Обновляем таблицу после закрытия формы
            refreshTable();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // === Фабрики значений для колонок ===

    // Фабрика значений для даты начала
    private class StartDateCellValueFactory implements Callback<TableColumn.CellDataFeatures<Operation, String>, ObservableValue<String>> {
        @Override
        public ObservableValue<String> call(TableColumn.CellDataFeatures<Operation, String> cellData) {
            Operation operation = cellData.getValue();
            String formattedDate = operation.getStartDate().format(dateFormatter);
            return new SimpleStringProperty(formattedDate);
        }
    }

    // Фабрика значений для даты окончания
    private class EndDateCellValueFactory implements Callback<TableColumn.CellDataFeatures<Operation, String>, ObservableValue<String>> {
        @Override
        public ObservableValue<String> call(TableColumn.CellDataFeatures<Operation, String> cellData) {
            Operation operation = cellData.getValue();
            String formattedDate = operation.getEndDate().format(dateFormatter);
            return new SimpleStringProperty(formattedDate);
        }
    }

    // Фабрика значений для автомобиля
    private class CarCellValueFactory implements Callback<TableColumn.CellDataFeatures<Operation, String>, ObservableValue<String>> {
        @Override
        public ObservableValue<String> call(TableColumn.CellDataFeatures<Operation, String> cellData) {
            return new SimpleStringProperty(cellData.getValue().getCar().toString());
        }
    }

    // Фабрика значений для клиента
    private class ClientCellValueFactory implements Callback<TableColumn.CellDataFeatures<Operation, String>, ObservableValue<String>> {
        @Override
        public ObservableValue<String> call(TableColumn.CellDataFeatures<Operation, String> cellData) {
            Operation operation = cellData.getValue();
            return new SimpleStringProperty(operation.getClient().getName());
        }
    }

    // Фабрика значений для стоимости
    private class TotalCostCellValueFactory implements Callback<TableColumn.CellDataFeatures<Operation, Number>, ObservableValue<Number>> {
        @Override
        public ObservableValue<Number> call(TableColumn.CellDataFeatures<Operation, Number> cellData) {
            Operation operation = cellData.getValue();
            return new SimpleIntegerProperty(operation.getTotalCost());
        }
    }

    // === Фабрики ячеек ===

    // Фабрика ячеек для колонки статуса
    private class StatusCellFactory implements Callback<TableColumn<Operation, String>, TableCell<Operation, String>> {
        @Override
        public TableCell<Operation, String> call(TableColumn<Operation, String> column) {
            return new StatusTableCell();
        }
    }

    // Ячейка статуса
    private class StatusTableCell extends TableCell<Operation, String> {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                setText(null);
                setStyle("");
                return;
            }

            Operation operation = (Operation) getTableRow().getItem();
            LocalDate today = LocalDate.now();

            // Определяем текст и стиль в зависимости от статуса
            if ("completed".equals(operation.getStatus())) {
                setText("ЗАВЕРШЕНО");
                getStyleClass().add("status-badge-completed");
            } else if ("active".equals(operation.getStatus())) {
                if (operation.getEndDate().isBefore(today)) {
                    setText("ПРОСРОЧЕНО");
                    getStyleClass().add("status-badge-overdue");
                } else {
                    setText("АРЕНДА");
                    getStyleClass().add("status-badge-active");
                }
            } else if ("pending".equals(operation.getStatus())) {
                setText("БРОНЬ");
                getStyleClass().add("status-badge-pending");
            } else {
                setText(operation.getStatus());
                setStyle("");
            }
        }
    }

    // Фабрика ячеек для колонки действий
    private class ActionsCellFactory implements Callback<TableColumn<Operation, Void>, TableCell<Operation, Void>> {
        @Override
        public TableCell<Operation, Void> call(TableColumn<Operation, Void> column) {
            return new ActionsTableCell();
        }
    }

    // Ячейка с кнопками действий
    private class ActionsTableCell extends TableCell<Operation, Void> {
        private Button actionButton = new Button();
        private HBox container = new HBox(actionButton);

        public ActionsTableCell() {
            container.setAlignment(Pos.CENTER);
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                setGraphic(null);
                return;
            }

            Operation operation = (Operation) getTableRow().getItem();
            actionButton.getStyleClass().clear();

            // Настраиваем кнопку в зависимости от статуса
            if ("pending".equals(operation.getStatus())) {
                // Кнопка "Выдать" для бронирований
                actionButton.setText("Выдать");
                actionButton.getStyleClass().add("operation-issue-button");
                // Делаем кнопку визуально кликабельной
                actionButton.setStyle("-fx-cursor: hand; -fx-font-weight: bold;");
                actionButton.setOnAction(new IssueCarHandler(operation));
            } else if ("active".equals(operation.getStatus())) {
                // Кнопка "Забрать" для активных аренд
                actionButton.setText("Забрать");
                actionButton.getStyleClass().add("operation-return-button");
                // Делаем кнопку визуально кликабельной
                actionButton.setStyle("-fx-cursor: hand; -fx-font-weight: bold;");
                actionButton.setOnAction(new ReturnCarHandler(operation));
            } else {
                // Для завершенных - кнопка не нужна
                actionButton.setText("");
                actionButton.setStyle("");
                actionButton.setOnAction(null);
            }

            setGraphic(actionButton);
        }
    }

    // Обработчик выдачи автомобиля
    private class IssueCarHandler implements EventHandler<ActionEvent> {
        private Operation operation;

        public IssueCarHandler(Operation operation) {
            this.operation = operation;
        }

        @Override
        public void handle(ActionEvent event) {
            // Проверка наличия паспортных данных у клиента
            org.example.carrent.model.Client client = operation.getClient();
            boolean hasPassportData = client.getPassportSeries() != null && !client.getPassportSeries().trim().isEmpty() &&
                                      client.getPassportNumber() != null && !client.getPassportNumber().trim().isEmpty();

            if (!hasPassportData) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                alert.setTitle("Внимание");
                alert.setHeaderText("Отсутствуют паспортные данные");
                alert.setContentText("У клиента не заполнены паспортные данные. Вы уверены, что хотите выдать автомобиль?");

                java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
                if (!result.isPresent() || result.get() != javafx.scene.control.ButtonType.OK) {
                    return;
                }
            }

            // Меняем статус операции на "active"
            OperationDB.updateStatus(operation.getId(), "active");
            // Меняем статус автомобиля на "rented"
            CarDB.updateStatus(operation.getCar().getId(), "rented");
            refreshTable();
        }
    }

    // Обработчик возврата автомобиля
    private class ReturnCarHandler implements EventHandler<ActionEvent> {
        private Operation operation;

        public ReturnCarHandler(Operation operation) {
            this.operation = operation;
        }

        @Override
        public void handle(ActionEvent event) {
            // Проверка на досрочный возврат
            if (LocalDate.now().isBefore(operation.getEndDate())) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                alert.setTitle("Внимание");
                alert.setHeaderText(null);
                alert.setContentText("Срок аренды ещё не истек (" + operation.getEndDate() + "). Вы уверены, что хотите оформить возврат?");
                
                java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
                if (!result.isPresent() || result.get() != javafx.scene.control.ButtonType.OK) {
                    return;
                }
            }

            try {
                // Открываем окно приемки автомобиля
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/carrent/return-car-view.fxml"));
                Parent parent = fxmlLoader.load();

                ReturnCarController controller = fxmlLoader.getController();
                controller.setOperation(operation);

                Scene scene = new Scene(parent, 300, 200);
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Приемка авто");
                stage.setScene(scene);
                stage.showAndWait();

                // Получаем результат приемки
                String carStatus = controller.getReturnStatus();

                // Если не отменено - завершаем операцию
                if (carStatus != null) {
                    CompletedOperationDB.addCompletedOperation(operation);
                    OperationDB.updateStatus(operation.getId(), "completed");
                    CarDB.updateStatus(operation.getCar().getId(), carStatus);
                    refreshTable();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }
}
