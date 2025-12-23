package org.example.carrent.model;

// Модель автомобиля
public class Car {

    // Уникальный идентификатор
    private int id;
    // Государственный номер
    private String plate;
    // Марка автомобиля
    private String make;
    // Модель автомобиля
    private String model;
    // Цвет
    private String color;
    // Мощность двигателя (л.с.)
    private int power;
    // Стоимость аренды за сутки (руб.)
    private int price;
    // Статус: 'free', 'rented', 'repair', 'reserved'
    private String status;
    // VIN-номер
    private String vin;

    // Полный конструктор
    public Car(int id, String plate, String make, String model, String color,
               int power, int price, String status, String vin) {
        this.id = id;
        this.plate = plate;
        this.make = make;
        this.model = model;
        this.color = color;
        this.power = power;
        this.price = price;
        this.status = status;
        this.vin = vin;
    }

    // Конструктор для нового автомобиля (id=0, status=free)
    public Car(String plate, String make, String model, String color,
               int power, int price, String vin) {
        this(0, plate, make, model, color, power, price, "free", vin);
    }

    // Конструктор для совместимости (без VIN)
    public Car(String plate, String make, String model, String color, int power, int price) {
        this(0, plate, make, model, color, power, price, "free", null);
    }

    // === Геттеры ===

    public int getId() {
        return id;
    }

    public String getPlate() {
        return plate;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getColor() {
        return color;
    }

    public int getPower() {
        return power;
    }

    public int getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public String getVin() {
        return vin;
    }

    // === Сеттеры ===

    public void setStatus(String status) {
        this.status = status;
    }

    // Строковое представление для ComboBox
    @Override
    public String toString() {
        return make + " " + model + " (" + plate + ")";
    }
}
