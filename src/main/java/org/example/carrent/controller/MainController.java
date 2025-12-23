package org.example.carrent.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

// Главный контроллер приложения - управляет навигацией между разделами
public class MainController {

    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private VBox sidebar;
    @FXML
    private Button bookingsNavButton;
    @FXML
    private Button fleetNavButton;
    @FXML
    private Button clientsNavButton;
    @FXML
    private Button completedNavButton;

    // Путь к текущему отображаемому представлению
    private String currentViewPath = "/org/example/carrent/operations-view.fxml";

    @FXML
    public void initialize() {
        // Устанавливаем начальное представление
        selectNavButton(bookingsNavButton);
        loadView(currentViewPath);
    }

    // Обработчик нажатия на кнопку "Автопарк"
    @FXML
    protected void onFleetButtonClick() {
        currentViewPath = "/org/example/carrent/fleet-view.fxml";
        loadView(currentViewPath);
        selectNavButton(fleetNavButton);
    }

    // Обработчик нажатия на кнопку "Журнал операций"
    @FXML
    protected void onBookingsButtonClick() {
        currentViewPath = "/org/example/carrent/operations-view.fxml";
        loadView(currentViewPath);
        selectNavButton(bookingsNavButton);
    }

    // Обработчик нажатия на кнопку "Клиенты"
    @FXML
    protected void onClientsButtonClick() {
        currentViewPath = "/org/example/carrent/clients-view.fxml";
        loadView(currentViewPath);
        selectNavButton(clientsNavButton);
    }

    // Обработчик нажатия на кнопку "Завершенные заказы"
    @FXML
    protected void onCompletedOrdersButtonClick() {
        currentViewPath = "/org/example/carrent/completed-orders-view.fxml";
        loadView(currentViewPath);
        selectNavButton(completedNavButton);
    }

    // Загрузка представления в центр BorderPane
    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            mainBorderPane.setCenter(view);
        } catch (IOException exception) {
            exception.printStackTrace();
            System.err.println("Не удалось загрузить view: " + fxmlPath);
        }
    }

    // Выделение активной кнопки навигации
    private void selectNavButton(Button selectedButton) {
        // Снимаем выделение со всех кнопок
        // (Опционально: можно добавить стиль для выделенной кнопки, если нужно)
    }
}
