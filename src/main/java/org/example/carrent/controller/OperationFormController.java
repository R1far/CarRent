package org.example.carrent.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.carrent.db.CarDB;
import org.example.carrent.db.ClientDB;
import org.example.carrent.db.OperationDB;
import org.example.carrent.model.Car;
import org.example.carrent.model.Client;
import org.example.carrent.model.Operation;
import org.example.carrent.utils.AlertUtils;

import java.time.LocalDate;
import java.util.List;

// Контроллер формы создания новой операции аренды
public class OperationFormController {

    @FXML
    private ComboBox<Car> carComboBox;
    @FXML
    private ComboBox<Client> clientComboBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Label totalCostLabel;

    @FXML
    public void initialize() {
        // Загружаем списки автомобилей и клиентов
        List<Car> carList = CarDB.getCars();
        List<Client> clientList = ClientDB.getClients();

        carComboBox.setItems(FXCollections.observableArrayList(carList));
        clientComboBox.setItems(FXCollections.observableArrayList(clientList));

        // Запрещаем ручной ввод дат
        startDatePicker.setEditable(false);
        endDatePicker.setEditable(false);

        // Запрещаем выбор прошедших дат
        startDatePicker.setDayCellFactory(new PastDateDisabler());
        endDatePicker.setDayCellFactory(new PastDateDisabler());

        // Добавляем слушатель изменения даты начала
        startDatePicker.valueProperty().addListener(new StartDateChangeListener());
    }

    // Фабрика ячеек для блокировки прошедших дат
    private class PastDateDisabler implements Callback<DatePicker, DateCell> {
        @Override
        public DateCell call(DatePicker picker) {
            return new DateCell() {
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

    // Фабрика ячеек для блокировки дат раньше даты начала
    private class EndDateDisabler implements Callback<DatePicker, DateCell> {
        private LocalDate startDate;

        public EndDateDisabler(LocalDate startDate) {
            this.startDate = startDate;
        }

        @Override
        public DateCell call(DatePicker picker) {
            return new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    // Блокируем даты раньше даты начала
                    if (empty || date.isBefore(startDate)) {
                        setDisable(true);
                    }
                }
            };
        }
    }

    // Слушатель изменения даты начала
    private class StartDateChangeListener implements ChangeListener<LocalDate> {
        @Override
        public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldDate, LocalDate newDate) {
            if (newDate != null) {
                // Обновляем ограничения для даты окончания
                endDatePicker.setDayCellFactory(new EndDateDisabler(newDate));

                // Если дата окончания раньше новой даты начала - сбрасываем
                LocalDate endDate = endDatePicker.getValue();
                if (endDate != null && endDate.isBefore(newDate)) {
                    endDatePicker.setValue(newDate);
                }
            } else {
                // Если дата начала очищена - возвращаем стандартные ограничения
                endDatePicker.setDayCellFactory(new PastDateDisabler());
            }

            // Пересчитываем стоимость
            calculateTotal();
        }
    }

    // Расчёт итоговой стоимости аренды
    @FXML
    protected void calculateTotal() {
        Car selectedCar = carComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        // Проверяем что все поля заполнены
        if (selectedCar == null || startDate == null || endDate == null) {
            totalCostLabel.setText("0 ₽");
            return;
        }

        // Вычисляем количество дней
        long startDay = startDate.toEpochDay();
        long endDay = endDate.toEpochDay();

        if (endDay < startDay) {
            totalCostLabel.setText("Ошибка даты");
            return;
        }

        long days = endDay - startDay;
        // Минимум 1 день
        if (days == 0) {
            days = 1;
        }

        // Считаем итоговую стоимость
        int totalCost = (int) (days * selectedCar.getPrice());
        totalCostLabel.setText(totalCost + " ₽");
    }

    // Сохранение операции
    @FXML
    protected void onSaveClick() {
        Car selectedCar = carComboBox.getValue();
        Client selectedClient = clientComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        // Валидация полей
        if (selectedCar == null || selectedClient == null || startDate == null || endDate == null) {
            AlertUtils.showError("Ошибка", "Заполните все поля!");
            return;
        }

        if (endDate.isBefore(startDate)) {
            AlertUtils.showError("Ошибка", "Дата возврата раньше даты выдачи!");
            return;
        }

        // Проверка занятости автомобиля
        if (OperationDB.isCarBusy(selectedCar.getId(), startDate, endDate)) {
            AlertUtils.showError("Ошибка", "Автомобиль уже занят на выбранные даты!");
            return;
        }

        // Проверка занятости клиента
        if (OperationDB.isClientBusy(selectedClient.getId(), startDate, endDate)) {
            AlertUtils.showError("Ошибка", "У клиента уже есть аренда в этот период!");
            return;
        }

        // Рассчитываем стоимость
        long days = endDate.toEpochDay() - startDate.toEpochDay();
        if (days == 0) {
            days = 1;
        }
        int totalCost = (int) (days * selectedCar.getPrice());

        // Определяем статус операции
        String status;
        if (startDate.equals(LocalDate.now())) {
            status = "active";
        } else {
            status = "pending";
        }

        // Создаем и сохраняем операцию
        Operation operation = new Operation(0, selectedCar, selectedClient, startDate, endDate, totalCost, status);
        OperationDB.addOperation(operation);

        // Обновляем статус автомобиля
        if ("active".equals(status)) {
            CarDB.updateStatus(selectedCar.getId(), "rented");
        } else {
            CarDB.updateStatus(selectedCar.getId(), "reserved");
        }

        // Закрываем окно
        Stage stage = (Stage) carComboBox.getScene().getWindow();
        stage.close();
    }
}
