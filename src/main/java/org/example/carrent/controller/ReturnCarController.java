package org.example.carrent.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.example.carrent.model.Operation;

// Контроллер окна приёмки автомобиля
public class ReturnCarController {

    @FXML
    private Button cancelButton;

    // Текущая операция
    private Operation currentOperation;

    // Статус возврата: null (отмена), "free" (исправен), "repair" (в ремонт)
    private String returnStatus = null;

    // Установка операции
    public void setOperation(Operation operation) {
        this.currentOperation = operation;
    }

    // Получение статуса возврата
    public String getReturnStatus() {
        return returnStatus;
    }

    // Автомобиль исправен - вернуть в строй
    @FXML
    private void onReturnOk() {
        returnStatus = "free";
        closeStage();
    }

    // Автомобиль требует ремонта
    @FXML
    private void onReturnRepair() {
        returnStatus = "repair";
        closeStage();
    }

    // Отмена приёмки
    @FXML
    private void onCancel() {
        returnStatus = null;
        closeStage();
    }

    // Закрытие окна
    private void closeStage() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
