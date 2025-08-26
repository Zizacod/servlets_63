package app.repository;

import app.model.Car;

import java.util.List;

public interface CarRepository {

    List<Car> getAll();

    Car save(Car car);

    Car getById(long id);
    
    Car update(Car car) throws IllegalArgumentException;
    
    boolean delete(long id);
}
