package org.example.carrent.db;

import org.example.carrent.model.Client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Класс для работы с таблицей клиентов в БД
public class ClientDB {

    // Инициализация таблицы при загрузке класса
    static {
        createTable();
    }

    // Создание таблицы clients, если она не существует
    private static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS clients (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "name TEXT NOT NULL, " +
                     "phone TEXT NOT NULL, " +
                     "passportSeries TEXT, " +
                     "passportNumber TEXT, " +
                     "issueDate TEXT, " +
                     "authority TEXT)";

        try {
            Connection connection = Database.connect();
            Statement statement = connection.createStatement();
            statement.execute(sql);
            statement.close();
            connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    // Получить клиента по ID
    public static Client getClient(int id) {
        String sql = "SELECT * FROM clients WHERE id = ?";

        try {
            Connection connection = Database.connect();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String phone = resultSet.getString("phone");
                String passportSeries = resultSet.getString("passportSeries");
                String passportNumber = resultSet.getString("passportNumber");
                String issueDateString = resultSet.getString("issueDate");
                LocalDate issueDate = null;
                if (issueDateString != null) {
                    issueDate = LocalDate.parse(issueDateString);
                }
                String authority = resultSet.getString("authority");

                Client client = new Client(id, name, phone, passportSeries, passportNumber, issueDate, authority);

                resultSet.close();
                preparedStatement.close();
                connection.close();
                return client;
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    // Получить список всех клиентов
    public static List<Client> getClients() {
        List<Client> clientList = new ArrayList<>();
        String sql = "SELECT id, name, phone, passportSeries, passportNumber, issueDate, authority FROM clients";

        try {
            Connection connection = Database.connect();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            // Перебираем все записи в результате
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String phone = resultSet.getString("phone");
                String passportSeries = resultSet.getString("passportSeries");
                String passportNumber = resultSet.getString("passportNumber");
                String issueDateString = resultSet.getString("issueDate");
                LocalDate issueDate = null;
                if (issueDateString != null) {
                    issueDate = LocalDate.parse(issueDateString);
                }
                String authority = resultSet.getString("authority");

                Client client = new Client(id, name, phone, passportSeries, passportNumber, issueDate, authority);
                clientList.add(client);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return clientList;
    }

    // Добавить нового клиента
    public static void addClient(Client client) {
        String sql = "INSERT INTO clients(name, phone, passportSeries, passportNumber, issueDate, authority) VALUES(?, ?, ?, ?, ?, ?)";

        try {
            Connection connection = Database.connect();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, client.getName());
            preparedStatement.setString(2, client.getPhone());
            preparedStatement.setString(3, client.getPassportSeries());
            preparedStatement.setString(4, client.getPassportNumber());

            // Преобразуем дату в строку
            String issueDateString = null;
            if (client.getIssueDate() != null) {
                issueDateString = client.getIssueDate().toString();
            }
            preparedStatement.setString(5, issueDateString);
            preparedStatement.setString(6, client.getAuthority());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    // Обновить данные клиента
    public static void updateClient(Client client) {
        String sql = "UPDATE clients SET name=?, phone=?, passportSeries=?, passportNumber=?, issueDate=?, authority=? WHERE id=?";

        try {
            Connection connection = Database.connect();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, client.getName());
            preparedStatement.setString(2, client.getPhone());
            preparedStatement.setString(3, client.getPassportSeries());
            preparedStatement.setString(4, client.getPassportNumber());

            // Преобразуем дату в строку
            String issueDateString = null;
            if (client.getIssueDate() != null) {
                issueDateString = client.getIssueDate().toString();
            }
            preparedStatement.setString(5, issueDateString);
            preparedStatement.setString(6, client.getAuthority());
            preparedStatement.setInt(7, client.getId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    // Удалить клиента по ID
    public static void deleteClient(int id) {
        String sql = "DELETE FROM clients WHERE id = ?";

        try {
            Connection connection = Database.connect();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
