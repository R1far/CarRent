package org.example.carrent.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.carrent.db.CarDB;
import org.example.carrent.model.Car;
import org.example.carrent.utils.AlertUtils;

// Контроллер формы добавления/редактирования автомобиля
public class CarFormController {

    @FXML
    private TextField plateField;
    @FXML
    private TextField makeField;
    @FXML
    private TextField modelField;
    @FXML
    private TextField colorField;
    @FXML
    private TextField powerField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField vinField;

    // Текущий редактируемый автомобиль (null для нового)
    private Car currentCar;

    // Установка автомобиля для редактирования
    public void setCar(Car car) {
        this.currentCar = car;

        // Если редактируем - заполняем поля
        if (car != null) {
            plateField.setText(car.getPlate());
            makeField.setText(car.getMake());
            modelField.setText(car.getModel());
            colorField.setText(car.getColor());
            powerField.setText(String.valueOf(car.getPower()));
            priceField.setText(String.valueOf(car.getPrice()));
            vinField.setText(car.getVin());
        }
    }

    // Отмена и закрытие формы
    @FXML
    private void onCancel() {
        Stage stage = (Stage) plateField.getScene().getWindow();
        stage.close();
    }

    // Сохранение автомобиля
    @FXML
    private void onSave() {
        try {
            // Получаем значения из полей
            String plate = plateField.getText().trim();
            String make = makeField.getText().trim();
            String model = modelField.getText().trim();
            String color = colorField.getText().trim();
            String vin = vinField.getText().trim();
            String powerStr = powerField.getText().trim();
            String priceStr = priceField.getText().trim();

            // Проверяем обязательные поля
            if (plate.isEmpty() || make.isEmpty() || model.isEmpty() || color.isEmpty() || vin.isEmpty() || powerStr.isEmpty() || priceStr.isEmpty()) {
                AlertUtils.showError("Ошибка", "Заполните все поля!");
                return;
            }

            // Валидация номера (А 000 АА 00 или А 000 АА 000) - русские буквы + регион
            // Разрешенные буквы: А, В, Е, К, М, Н, О, Р, С, Т, У, Х (и их латинские аналоги)
            // Формат: Буква + 3 цифры + 2 буквы + (опционально пробел) + 2 или 3 цифры региона
            if (!plate.matches("^[АВЕКМНОРСТУХABEKMHOPCTYXавекмнорстухabekmhopctyx]\\d{3}[АВЕКМНОРСТУХABEKMHOPCTYXавекмнорстухabekmhopctyx]{2}\\s?\\d{2,3}$")) {
                AlertUtils.showError("Ошибка", "Неверный гос-номер! Формат: А000АА 77 (или 777). Обязательно укажите регион!");
                return;
            }

            // Валидация VIN (17 символов, без I, O, Q)
            if (!vin.matches("^[A-HJ-NPR-Z0-9a-hj-npr-z]{17}$")) {
                AlertUtils.showError("Ошибка", "Неверный VIN! Должно быть 17 символов (цифры и латиница, кроме I, O, Q).");
                return;
            }

            // Валидация мощности (только цифры)
            if (!powerStr.matches("^\\d+$")) {
                AlertUtils.showError("Ошибка", "Мощность должна содержать только цифры!");
                return;
            }

            // Валидация цены (только цифры)
            if (!priceStr.matches("^\\d+$")) {
                AlertUtils.showError("Ошибка", "Цена должна содержать только цифры!");
                return;
            }

            // Парсим числовые поля
            int power = Integer.parseInt(powerStr);
            int price = Integer.parseInt(priceStr);

            if (currentCar != null) {
                // Обновляем существующий автомобиль
                Car updatedCar = new Car(
                    currentCar.getId(),
                    plate.toUpperCase(), // Приводим номер к верхнему регистру
                    make,
                    model,
                    color,
                    power,
                    price,
                    currentCar.getStatus(),
                    vin.toUpperCase() // VIN тоже в верхний регистр
                );
                CarDB.updateCar(updatedCar);
            } else {
                // Создаём новый автомобиль
                Car newCar = new Car(plate.toUpperCase(), make, model, color, power, price, vin.toUpperCase());
                CarDB.addCar(newCar);
            }

            // Закрываем форму
            onCancel();

        } catch (NumberFormatException exception) {
            AlertUtils.showError("Ошибка", "Мощность и цена должны быть числами!");
        } catch (Exception exception) {
            exception.printStackTrace();
            AlertUtils.showError("Ошибка", "Ошибка сохранения: " + exception.getMessage());
        }
    }
}
