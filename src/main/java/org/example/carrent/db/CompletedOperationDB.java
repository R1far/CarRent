package org.example.carrent.db;

import org.example.carrent.model.Car;
import org.example.carrent.model.Client;
import org.example.carrent.model.Operation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Класс для работы с таблицей завершенных операций в БД
public class CompletedOperationDB {

    // Инициализация таблицы при загрузке класса
    static {
        createTable();
    }

    // Создание таблицы completed_operations, если она не существует
    private static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS completed_operations (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "car_id INTEGER, " +
                     "client_id INTEGER, " +
                     "start_date TEXT, " +
                     "end_date TEXT, " +
                     "total_cost INTEGER)";

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

    // Добавить завершенную операцию
    public static void addCompletedOperation(Operation operation) {
        String sql = "INSERT INTO completed_operations(car_id, client_id, start_date, end_date, total_cost) VALUES(?, ?, ?, ?, ?)";

        try {
            Connection connection = Database.connect();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, operation.getCar().getId());
            preparedStatement.setInt(2, operation.getClient().getId());
            preparedStatement.setString(3, operation.getStartDate().toString());
            preparedStatement.setString(4, operation.getEndDate().toString());
            preparedStatement.setInt(5, operation.getTotalCost());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    // Получить список всех завершенных операций
    public static List<Operation> getCompletedOperations() {
        List<Operation> operationList = new ArrayList<>();
        String sql = "SELECT * FROM completed_operations";

        try {
            Connection connection = Database.connect();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            // Перебираем все записи
            while (resultSet.next()) {
                int carId = resultSet.getInt("car_id");
                int clientId = resultSet.getInt("client_id");

                // Получаем связанные объекты
                Car car = CarDB.getCar(carId);
                Client client = ClientDB.getClient(clientId);

                // Добавляем только если машина и клиент существуют
                if (car != null && client != null) {
                    Operation operation = new Operation(
                        resultSet.getInt("id"),
                        car,
                        client,
                        LocalDate.parse(resultSet.getString("start_date")),
                        LocalDate.parse(resultSet.getString("end_date")),
                        resultSet.getInt("total_cost"),
                        "completed"
                    );
                    operationList.add(operation);
                }
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return operationList;
    }
}
