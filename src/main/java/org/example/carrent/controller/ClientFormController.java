package org.example.carrent.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.carrent.db.ClientDB;
import org.example.carrent.model.Client;
import org.example.carrent.utils.AlertUtils;

import java.time.LocalDate;

// Контроллер формы добавления/редактирования клиента
public class ClientFormController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField passportSeriesField;
    @FXML
    private TextField passportNumberField;
    @FXML
    private DatePicker issueDateField;
    @FXML
    private TextField authorityField;
    @FXML
    private Button cancelButton;

    // Текущий редактируемый клиент (null для нового)
    private Client currentClient;

    @FXML
    public void initialize() {
        // Разрешаем ручной ввод даты
        issueDateField.setEditable(true);
    }

    // Установка клиента для редактирования
    public void setClient(Client client) {
        this.currentClient = client;

        // Если редактируем - заполняем поля
        if (client != null) {
            nameField.setText(client.getName());
            phoneField.setText(client.getPhone());
            passportSeriesField.setText(client.getPassportSeries());
            passportNumberField.setText(client.getPassportNumber());
            issueDateField.setValue(client.getIssueDate());
            authorityField.setText(client.getAuthority());
        }
    }

    // Отмена и закрытие формы
    @FXML
    private void onCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    // Сохранение клиента
    @FXML
    private void onSave() {
        // Получаем значения из полей
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String passportSeries = passportSeriesField.getText().trim();
        String passportNumber = passportNumberField.getText().trim();
        LocalDate issueDate = issueDateField.getValue();
        String dateText = issueDateField.getEditor().getText().trim();
        String authority = authorityField.getText().trim();

        // 1. Обязательная проверка: ФИО
        if (name.isEmpty()) {
            AlertUtils.showError("Ошибка", "Введите ФИО!");
            return;
        }
        if (name.matches(".*\\d.*")) {
            AlertUtils.showError("Ошибка", "ФИО не должно содержать цифр!");
            return;
        }

        // 2. Обязательная проверка: Телефон
        if (phone.isEmpty()) {
            AlertUtils.showError("Ошибка", "Введите телефон!");
            return;
        }
        if (!phone.matches("^\\d+$")) {
            AlertUtils.showError("Ошибка", "Телефон должен содержать только цифры!");
            return;
        }

        // 3. Опциональная проверка: Паспортные данные (только если заполнены)
        if (!passportSeries.isEmpty()) {
            // Если начали вводить, проверяем формат
            if (!passportSeries.matches("^\\d{2} \\d{2}$")) {
                AlertUtils.showError("Ошибка", "Серия паспорта должна быть в формате XX XX (4 цифры с пробелом)!");
                return;
            }
        }

        if (!passportNumber.isEmpty()) {
             // Если начали вводить, проверяем формат
            if (!passportNumber.matches("^\\d{6}$")) {
                AlertUtils.showError("Ошибка", "Номер паспорта должен состоять из 6 цифр!");
                return;
            }
        }

        // Валидация даты (техническая проверка парсинга)
        if (issueDate == null && !dateText.isEmpty()) {
            AlertUtils.showError("Ошибка", "Неверный формат даты! Используйте дд.мм.гггг");
            return;
        }
        // Дата выдачи не может быть в будущем
        if (issueDate != null) {
             if (issueDate.isAfter(LocalDate.now())) {
                 AlertUtils.showError("Ошибка", "Дата выдачи не может быть в будущем!");
                 return;
             }
        }

        if (currentClient != null) {
            // Обновляем существующего клиента
            Client updatedClient = new Client(
                currentClient.getId(),
                name,
                phone,
                passportSeries,
                passportNumber,
                issueDate,
                authority
            );
            ClientDB.updateClient(updatedClient);
        } else {
            // Создаём нового клиента
            Client newClient = new Client(
                name,
                phone,
                passportSeries,
                passportNumber,
                issueDate,
                authority
            );
            ClientDB.addClient(newClient);
        }

        // Закрываем форму
        onCancel();
    }
}
