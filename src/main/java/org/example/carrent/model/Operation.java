package org.example.carrent.model;

import java.time.LocalDate;

// Модель операции аренды
public class Operation {

    // Уникальный идентификатор
    private int id;
    // Арендуемый автомобиль
    private Car car;
    // Клиент-арендатор
    private Client client;
    // Дата начала аренды
    private LocalDate startDate;
    // Дата окончания аренды
    private LocalDate endDate;
    // Итоговая стоимость (руб.)
    private int totalCost;
    // Статус: 'active', 'completed', 'pending'
    private String status;

    // Полный конструктор
    public Operation(int id, Car car, Client client, LocalDate startDate,
                     LocalDate endDate, int totalCost, String status) {
        this.id = id;
        this.car = car;
        this.client = client;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalCost = totalCost;
        this.status = status;
    }

    // Конструктор для новой операции (статус по умолчанию - pending)
    public Operation(Car car, Client client, LocalDate startDate, LocalDate endDate, int totalCost) {
        this(0, car, client, startDate, endDate, totalCost, "pending");
    }

    // === Геттеры ===

    public int getId() {
        return id;
    }

    public Car getCar() {
        return car;
    }

    public Client getClient() {
        return client;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public String getStatus() {
        return status;
    }
}
