package org.example.carrent.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.carrent.db.CarDB;
import org.example.carrent.model.Car;

import java.io.IOException;
import java.util.List;

// Контроллер раздела "Автопарк" - отображает карточки автомобилей
public class FleetController {

    @FXML
    private FlowPane carsContainer;

    @FXML
    public void initialize() {
        refreshGrid();
    }

    // Обновление сетки карточек автомобилей
    private void refreshGrid() {
        carsContainer.getChildren().clear();
        List<Car> carList = CarDB.getCars();

        // Добавляем карточку для каждого автомобиля
        for (Car car : carList) {
            VBox carCard = createCarCard(car);
            carsContainer.getChildren().add(carCard);
        }
    }

    // Обработчик кнопки добавления автомобиля
    @FXML
    protected void onAddCarClick() {
        openCarForm(null);
    }

    // Открытие формы редактирования/добавления автомобиля
    private void openCarForm(Car car) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/carrent/car-form-view.fxml"));
            Parent parent = fxmlLoader.load();

            CarFormController controller = fxmlLoader.getController();
            controller.setCar(car);

            Scene scene = new Scene(parent, 340, 450);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);

            // Заголовок зависит от режима
            if (car == null) {
                stage.setTitle("Новый автомобиль");
            } else {
                stage.setTitle("Редактирование авто");
            }

            stage.setScene(scene);
            stage.showAndWait();

            // Обновляем сетку после закрытия формы
            refreshGrid();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Открытие окна истории бронирований автомобиля
    private void openCarHistory(Car car) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/carrent/car-history-view.fxml"));
            Parent parent = fxmlLoader.load();

            CarHistoryController controller = fxmlLoader.getController();
            controller.setCar(car);

            Scene scene = new Scene(parent, 400, 500);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("История бронирований");
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Создание карточки автомобиля
    private VBox createCarCard(Car car) {
        VBox card = new VBox();
        card.setPrefWidth(280);
        card.setStyle(
                "-fx-background-color: white; " +
                "-fx-background-radius: 12; " +
                "-fx-border-color: #e2e8f0; " +
                "-fx-border-radius: 12; " +
                "-fx-border-width: 1; " +
                "-fx-padding: 20; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);"
        );

        // === Заголовок карточки: номер + кнопки ===
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-padding: 0 0 16 0;");

        Label plateLabel = new Label(car.getPlate());
        plateLabel.setStyle(
                "-fx-border-color: #1e293b; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 4; " +
                "-fx-background-color: white; " +
                "-fx-text-fill: #0f172a; " +
                "-fx-font-family: 'Monospaced'; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 2 8; " +
                "-fx-font-size: 14px;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Кнопка редактирования
        Button editButton = new Button("✎");
        editButton.setStyle(
                "-fx-background-color: white; " +
                "-fx-border-color: #e2e8f0; " +
                "-fx-border-radius: 4; " +
                "-fx-text-fill: #94a3b8; " +
                "-fx-cursor: hand; " +
                "-fx-font-size: 12px; " +
                "-fx-padding: 4 8;"
        );
        editButton.setOnAction(new EditCarHandler(car));

        // Кнопка истории
        Button historyButton = new Button("\uD83D\uDCC5");
        historyButton.setStyle(
                "-fx-background-color: white; " +
                "-fx-border-color: #e2e8f0; " +
                "-fx-border-radius: 4; " +
                "-fx-text-fill: #94a3b8; " +
                "-fx-cursor: hand; " +
                "-fx-font-size: 12px; " +
                "-fx-padding: 4 8;"
        );
        historyButton.setOnAction(new ShowHistoryHandler(car));

        // Кнопка удаления
        Button deleteButton = new Button("🗑");
        deleteButton.setStyle(
                "-fx-background-color: white; " +
                "-fx-border-color: #e2e8f0; " +
                "-fx-border-radius: 4; " +
                "-fx-text-fill: #ef4444; " + // Красный цвет
                "-fx-cursor: hand; " +
                "-fx-font-size: 12px; " +
                "-fx-padding: 4 8;"
        );
        deleteButton.setOnAction(new DeleteCarHandler(car));

        HBox buttonGroup = new HBox(5);
        buttonGroup.getChildren().add(editButton);
        buttonGroup.getChildren().add(historyButton);
        buttonGroup.getChildren().add(deleteButton);

        header.getChildren().add(plateLabel);
        header.getChildren().add(spacer);
        header.getChildren().add(buttonGroup);

        // === Бейдж ремонта (если автомобиль в ремонте) ===
        if ("repair".equals(car.getStatus())) {
            Label repairBadge = new Label("РЕМОНТ");
            repairBadge.setStyle(
                    "-fx-background-color: #cbd5e1; " +
                    "-fx-text-fill: #475569; " +
                    "-fx-font-weight: bold; " +
                    "-fx-font-size: 10px; " +
                    "-fx-padding: 2 6; " +
                    "-fx-background-radius: 4;"
            );
            // Добавляем небольшой отступ снизу
            VBox badgeContainer = new VBox(repairBadge);
            badgeContainer.setStyle("-fx-padding: 0 0 8 0;");
            card.getChildren().add(badgeContainer);
        }

        // === Информация об автомобиле ===
        VBox infoBox = new VBox(4);
        infoBox.setStyle("-fx-padding: 0 0 16 0;");
        
        Label titleLabel = new Label(car.getMake() + " " + car.getModel());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        Label detailsLabel = new Label(car.getColor() + " • " + car.getPower() + " л.с.");
        detailsLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #64748b; -fx-text-transform: uppercase;");

        infoBox.getChildren().add(titleLabel);
        infoBox.getChildren().add(detailsLabel);

        // === Подвал карточки: цена + кнопка статуса ===
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setStyle("-fx-border-color: #f1f5f9; -fx-border-width: 1 0 0 0; -fx-padding: 12 0 0 0;");

        Label priceLabel = new Label(car.getPrice() + " ₽");
        priceLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label perDayLabel = new Label(" /сутки");
        perDayLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");

        HBox priceBox = new HBox();
        priceBox.getChildren().add(priceLabel);
        priceBox.getChildren().add(perDayLabel);
        priceBox.setAlignment(Pos.BASELINE_LEFT);

        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);

        // Кнопка изменения статуса
        Button statusButton = new Button();
        
        if ("repair".equals(car.getStatus())) {
            // Если в ремонте - кнопка "В строй"
            statusButton.setText("В строй");
            statusButton.setStyle(
                    "-fx-background-color: #0f172a; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-weight: bold; " +
                    "-fx-font-size: 12px; " +
                    "-fx-padding: 6 12; " +
                    "-fx-background-radius: 6; " +
                    "-fx-cursor: hand;"
            );
            statusButton.setOnAction(new SetFreeStatusHandler(car));
        } else {
            // Иначе - кнопка отправки в ремонт
            statusButton.setText("🛠");
            statusButton.setStyle(
                    "-fx-background-color: #f8fafc; " +
                    "-fx-text-fill: #64748b; " +
                    "-fx-border-color: #e2e8f0; " +
                    "-fx-border-radius: 6; " +
                    "-fx-background-radius: 6; " +
                    "-fx-cursor: hand; " +
                    "-fx-padding: 6 10;"
            );
            statusButton.setOnAction(new SetRepairStatusHandler(car));
        }

        footer.getChildren().add(priceBox);
        footer.getChildren().add(footerSpacer);
        footer.getChildren().add(statusButton);

        // Собираем карточку
        card.getChildren().add(header);
        card.getChildren().add(infoBox);
        card.getChildren().add(footer);
        
        // Если ремонт, делаем карточку чуть прозрачнее (как в дизайне)
        if ("repair".equals(car.getStatus())) {
             card.setStyle(card.getStyle() + " -fx-background-color: #f8fafc;");
        }

        return card;
    }

    // Обработчик кнопки редактирования автомобиля
    private class EditCarHandler implements EventHandler<ActionEvent> {
        private Car car;

        public EditCarHandler(Car car) {
            this.car = car;
        }

        @Override
        public void handle(ActionEvent event) {
            openCarForm(car);
        }
    }

    // Обработчик кнопки истории автомобиля
    private class ShowHistoryHandler implements EventHandler<ActionEvent> {
        private Car car;

        public ShowHistoryHandler(Car car) {
            this.car = car;
        }

        @Override
        public void handle(ActionEvent event) {
            openCarHistory(car);
        }
    }

    // Обработчик установки статуса "свободен"
    private class SetFreeStatusHandler implements EventHandler<ActionEvent> {
        private Car car;

        public SetFreeStatusHandler(Car car) {
            this.car = car;
        }

        @Override
        public void handle(ActionEvent event) {
            CarDB.updateStatus(car.getId(), "free");
            refreshGrid();
        }
    }

    // Обработчик установки статуса "в ремонте"
    private class SetRepairStatusHandler implements EventHandler<ActionEvent> {
        private Car car;

        public SetRepairStatusHandler(Car car) {
            this.car = car;
        }

        @Override
        public void handle(ActionEvent event) {
            CarDB.updateStatus(car.getId(), "repair");
            refreshGrid();
        }
    }

    // Обработчик удаления автомобиля
    private class DeleteCarHandler implements EventHandler<ActionEvent> {
        private Car car;

        public DeleteCarHandler(Car car) {
            this.car = car;
        }

        @Override
        public void handle(ActionEvent event) {
            // Спрашиваем подтверждение
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
            alert.setTitle("Удаление");
            alert.setHeaderText(null);
            alert.setContentText("Вы уверены, что хотите удалить автомобиль " + car.getPlate() + "?");

            java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                CarDB.deleteCar(car.getId());
                refreshGrid();
            }
        }
    }
}
