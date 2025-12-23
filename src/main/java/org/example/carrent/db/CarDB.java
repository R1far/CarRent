package org.example.carrent.db;

import org.example.carrent.model.Car;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// Класс для работы с таблицей автомобилей в БД
public class CarDB {

    // Инициализация таблицы при загрузке класса
    static {
        createTable();
    }

    // Создание таблицы cars, если она не существует
    private static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS cars (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "plate TEXT NOT NULL, " +
                     "make TEXT NOT NULL, " +
                     "model TEXT NOT NULL, " +
                     "color TEXT, " +
                     "power INTEGER, " +
                     "price INTEGER, " +
                     "status TEXT DEFAULT 'free', " +
                     "vin TEXT)";

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

    // Получить автомобиль по ID
    public static Car getCar(int id) {
        String sql = "SELECT * FROM cars WHERE id = ?";

        try {
            Connection connection = Database.connect();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Car car = new Car(
                    resultSet.getInt("id"),
                    resultSet.getString("plate"),
                    resultSet.getString("make"),
                    resultSet.getString("model"),
                    resultSet.getString("color"),
                    resultSet.getInt("power"),
                    resultSet.getInt("price"),
                    resultSet.getString("status"),
                    resultSet.getString("vin")
                );
                resultSet.close();
                preparedStatement.close();
                connection.close();
                return car;
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    // Получить список всех автомобилей
    public static List<Car> getCars() {
        List<Car> carList = new ArrayList<>();
        String sql = "SELECT * FROM cars";

        try {
            Connection connection = Database.connect();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            // Перебираем все записи в результате
            while (resultSet.next()) {
                Car car = new Car(
                    resultSet.getInt("id"),
                    resultSet.getString("plate"),
                    resultSet.getString("make"),
                    resultSet.getString("model"),
                    resultSet.getString("color"),
                    resultSet.getInt("power"),
                    resultSet.getInt("price"),
                    resultSet.getString("status"),
                    resultSet.getString("vin")
                );
                carList.add(car);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return carList;
    }

    // Добавить новый автомобиль
    public static void addCar(Car car) {
        String sql = "INSERT INTO cars(plate, make, model, color, power, price, status, vin) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection connection = Database.connect();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, car.getPlate());
            preparedStatement.setString(2, car.getMake());
            preparedStatement.setString(3, car.getModel());
            preparedStatement.setString(4, car.getColor());
            preparedStatement.setInt(5, car.getPower());
            preparedStatement.setInt(6, car.getPrice());
            preparedStatement.setString(7, car.getStatus());
            preparedStatement.setString(8, car.getVin());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    // Обновить статус автомобиля
    public static void updateStatus(int id, String status) {
        String sql = "UPDATE cars SET status = ? WHERE id = ?";

        try {
            Connection connection = Database.connect();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    // Обновить данные автомобиля
    public static void updateCar(Car car) {
        String sql = "UPDATE cars SET plate=?, make=?, model=?, color=?, power=?, price=?, vin=? WHERE id=?";

        try {
            Connection connection = Database.connect();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, car.getPlate());
            preparedStatement.setString(2, car.getMake());
            preparedStatement.setString(3, car.getModel());
            preparedStatement.setString(4, car.getColor());
            preparedStatement.setInt(5, car.getPower());
            preparedStatement.setInt(6, car.getPrice());
            preparedStatement.setString(7, car.getVin());
            preparedStatement.setInt(8, car.getId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    // Удалить автомобиль по ID
    public static void deleteCar(int id) {
        String sql = "DELETE FROM cars WHERE id = ?";

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
