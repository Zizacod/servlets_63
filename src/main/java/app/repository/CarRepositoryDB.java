package app.repository;

import app.constants.Constants;
import app.model.Car;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static app.constants.Constants.*;

public class CarRepositoryDB implements CarRepository {


    private Connection getConnection()  {

        try {
            Class.forName(DB_DRIVER_PATH);

            String dbUrl = String.format("%s%s?user=%s&password=%s",
                    DB_ADDRESS, DB_NAME, DB_USER, DB_PASSWORD);

            return DriverManager.getConnection(dbUrl);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }



    }


    @Override
    public List<Car> getAll() {
        try (Connection connection = getConnection()) {

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return List.of();
    }




    @Override
    public Car save(Car car) {

        try (Connection connection = getConnection()) {

            String query = String.format("INSERT INTO car (brand, price, year) VALUES ('%s', %s, %d);",
                    car.getBrand(), car.getPrice(), car.getYear());


            Statement statement = connection.createStatement();

           statement.execute(query, Statement.RETURN_GENERATED_KEYS);

            ResultSet resultSet = statement.getGeneratedKeys();

            resultSet.next();

            Long id = resultSet.getLong("id");

            car.setId(id);

            return car;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }






    @Override
    public Car getById(long id) {
        try (Connection connection = getConnection()) {

            String query = String.format("SELECT * from car where id=%d;", id);

            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                // есть строка с авто в result-set
                String brand = resultSet.getString("brand");
                BigDecimal price = resultSet.getBigDecimal("price");
                int year = resultSet.getInt("year");

                return new Car(id, brand, price, year);
            }

            // ответ пустой - автомобиль не найден
            return null;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Car update(Car car) throws IllegalArgumentException {
        try (Connection connection = getConnection()) {

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Car delete(long id) {

        // DELETE FROM WHERE

        try (Connection connection = getConnection()) {

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
