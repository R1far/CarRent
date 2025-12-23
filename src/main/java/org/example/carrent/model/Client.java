package org.example.carrent.model;

import java.time.LocalDate;

// Модель клиента
public class Client {

    // Уникальный идентификатор
    private int id;
    // ФИО клиента
    private String name;
    // Номер телефона
    private String phone;
    // Серия паспорта
    private String passportSeries;
    // Номер паспорта
    private String passportNumber;
    // Дата выдачи паспорта
    private LocalDate issueDate;
    // Орган, выдавший паспорт
    private String authority;

    // Полный конструктор
    public Client(int id, String name, String phone, String passportSeries,
                  String passportNumber, LocalDate issueDate, String authority) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.passportSeries = passportSeries;
        this.passportNumber = passportNumber;
        this.issueDate = issueDate;
        this.authority = authority;
    }

    // Конструктор для нового клиента (id=0)
    public Client(String name, String phone, String passportSeries,
                  String passportNumber, LocalDate issueDate, String authority) {
        this(0, name, phone, passportSeries, passportNumber, issueDate, authority);
    }

    // Конструктор для совместимости (только имя и телефон)
    public Client(String name, String phone) {
        this(0, name, phone, null, null, null, null);
    }

    // Конструктор для совместимости (с id)
    public Client(int id, String name, String phone) {
        this(id, name, phone, null, null, null, null);
    }

    // === Геттеры ===

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassportSeries() {
        return passportSeries;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public String getAuthority() {
        return authority;
    }

    // Строковое представление для ComboBox
    @Override
    public String toString() {
        return name;
    }
}
