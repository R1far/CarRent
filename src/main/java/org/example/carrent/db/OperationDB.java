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

// Класс для работы с таблицей операций аренды в БД
public class OperationDB {

    // Инициализация таблицы при загрузке класса
    static {
        createTable();
    }

    // Создание таблицы operations, если она не существует
    private static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS operations (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "car_id INTEGER, " +
                     "client_id INTEGER, " +
                     "start_date TEXT, " +
                     "end_date TEXT, " +
                     "total_cost INTEGER, " +
                     "status TEXT)";

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

    // Добавить новую операцию аренды
    public static void addOperation(Operation operation) {
        String sql = "INSERT INTO operations(car_id, client_id, start_date, end_date, total_cost, status) VALUES(?, ?, ?, ?, ?, ?)";

        try {
            Connection connection = Database.connect();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, operation.getCar().getId());
            preparedStatement.setInt(2, operation.getClient().getId());
            preparedStatement.setString(3, operation.getStartDate().toString());
            preparedStatement.setString(4, operation.getEndDate().toString());
            preparedStatement.setInt(5, operation.getTotalCost());
            preparedStatement.setString(6, operation.getStatus());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    // Получить список операций с расширенной фильтрацией (статус, даты)
    public static List<Operation> getFilteredOperations(String status, LocalDate dateFrom, LocalDate dateTo, boolean includeCompleted) {
        List<Operation> operationList = new ArrayList<>();
        
        // Базовый SQL
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM operations WHERE 1=1");
        
        // Добавляем условия
        if (status != null && !status.isEmpty() && !status.equals("Все")) {
            sqlBuilder.append(" AND status = '").append(status).append("'");
        } else if (!includeCompleted) {
             // Если статус не выбран (или "Все"), и завершенные не нужны - исключаем их
             sqlBuilder.append(" AND status != 'completed'");
        }
        
        // Фильтр по дате начала: с какой даты
        if (dateFrom != null) {
            sqlBuilder.append(" AND start_date >= '").append(dateFrom.toString()).append("'");
        }
        
        // Фильтр по дате начала: по какую дату
        if (dateTo != null) {
            sqlBuilder.append(" AND start_date <= '").append(dateTo.toString()).append("'");
        }
        
        // Сортировка по дате (сначала новые)
        sqlBuilder.append(" ORDER BY start_date DESC");

        try {
            Connection connection = Database.connect();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlBuilder.toString());

            // Перебираем записи
            while (resultSet.next()) {
                int carId = resultSet.getInt("car_id");
                int clientId = resultSet.getInt("client_id");

                // Загрузка зависимостей
                Car car = CarDB.getCar(carId);
                Client client = ClientDB.getClient(clientId);

                // Заглушки если удалены
                if (car == null) {
                    car = new Car(carId, "?", "Неизвестное", "Авто", "Нет", 0, 0, "unknown", "?");
                }
                if (client == null) {
                    client = new Client(clientId, "Удаленный клиент", "Нет", "", "", null, "");
                }

                try {
                    Operation operation = new Operation(
                        resultSet.getInt("id"),
                        car,
                        client,
                        LocalDate.parse(resultSet.getString("start_date")),
                        LocalDate.parse(resultSet.getString("end_date")),
                        resultSet.getInt("total_cost"),
                        resultSet.getString("status")
                    );
                    operationList.add(operation);
                } catch (Exception e) {
                    System.err.println("Ошибка чтения операции ID=" + resultSet.getInt("id"));
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

    // Получить список операций с фильтрацией (старый метод)
    public static List<Operation> getOperations(Integer filterCarId, boolean includeCompleted) {
        List<Operation> operationList = new ArrayList<>();

        // Формируем SQL запрос с условиями
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM operations");
        List<String> conditions = new ArrayList<>();

        if (filterCarId != null) {
            conditions.add("car_id = " + filterCarId);
        }
        if (!includeCompleted) {
            conditions.add("status != 'completed'");
        }

        // Добавляем WHERE если есть условия
        if (!conditions.isEmpty()) {
            sqlBuilder.append(" WHERE ");
            // Объединяем условия через AND
            for (int i = 0; i < conditions.size(); i++) {
                if (i > 0) {
                    sqlBuilder.append(" AND ");
                }
                sqlBuilder.append(conditions.get(i));
            }
        }

        try {
            Connection connection = Database.connect();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlBuilder.toString());

            // Перебираем все записи
            while (resultSet.next()) {
                int carId = resultSet.getInt("car_id");
                int clientId = resultSet.getInt("client_id");

                // Получаем связанные объекты
                Car car = CarDB.getCar(carId);
                Client client = ClientDB.getClient(clientId);

                // Если машина или клиент удалены - создаем заглушки, чтобы операцию можно было завершить
                if (car == null) {
                    car = new Car(carId, "?", "Неизвестное", "Авто", "Нет", 0, 0, "unknown", "?");
                }
                if (client == null) {
                    client = new Client(clientId, "Удаленный клиент", "Нет", "", "", null, "");
                }

                try {
                    Operation operation = new Operation(
                        resultSet.getInt("id"),
                        car,
                        client,
                        LocalDate.parse(resultSet.getString("start_date")),
                        LocalDate.parse(resultSet.getString("end_date")),
                        resultSet.getInt("total_cost"),
                        resultSet.getString("status")
                    );
                    operationList.add(operation);
                } catch (Exception e) {
                    System.err.println("Ошибка чтения операции ID=" + resultSet.getInt("id") + ": " + e.getMessage());
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

    // Получить операции по ID автомобиля (включая завершенные)
    public static List<Operation> getOperations(Integer filterCarId) {
        return getOperations(filterCarId, true);
    }

    // Обновить статус операции
    public static void updateStatus(int operationId, String newStatus) {
        String sql = "UPDATE operations SET status = ? WHERE id = ?";

        try {
            Connection connection = Database.connect();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, newStatus);
            preparedStatement.setInt(2, operationId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    // Проверка занятости автомобиля на указанный период
    public static boolean isCarBusy(int carId, LocalDate start, LocalDate end) {
        String sql = "SELECT COUNT(*) FROM operations WHERE car_id = ? AND status IN ('active', 'pending') AND start_date <= ? AND end_date >= ?";
        try (Connection connection = Database.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            
            preparedStatement.setInt(1, carId);
            preparedStatement.setString(2, end.toString());
            preparedStatement.setString(3, start.toString());
            
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    // Проверка занятости клиента на указанный период
    public static boolean isClientBusy(int clientId, LocalDate start, LocalDate end) {
        String sql = "SELECT COUNT(*) FROM operations WHERE client_id = ? AND status IN ('active', 'pending') AND start_date <= ? AND end_date >= ?";
        try (Connection connection = Database.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, clientId);
            preparedStatement.setString(2, end.toString());
            preparedStatement.setString(3, start.toString());

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }
}
