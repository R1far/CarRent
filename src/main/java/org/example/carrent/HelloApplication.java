package org.example.carrent;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

// Главный класс приложения CarRent
public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Загружаем главное представление
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);

        // Настраиваем окно
        stage.setTitle("CarRent");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    // Точка входа в приложение
    public static void main(String[] args) {
        launch();
    }
}
