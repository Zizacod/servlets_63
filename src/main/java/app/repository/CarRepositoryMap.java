package app.repository;

import app.model.Car;

import java.math.BigDecimal;
import java.util.*;

public class CarRepositoryMap implements CarRepository {

    public CarRepositoryMap() {
        initData();
    }

    private void initData(){
        save(new Car("VW", new BigDecimal(15000),2015));
        save(new Car("Mazda", new BigDecimal(30000),2022));
        save(new Car("Ford", new BigDecimal(40000),2024));
    }


    private Map<Long, Car> database = new HashMap<>();
    private long currentId;

    @Override
    public List<Car> getAll() {
      //  return new ArrayList<>(database.values());
        return database.values().stream().toList();
    }

    @Override
    public Car save(Car car) {
     car.setId(++currentId);
     database.put(car.getId(),car);
     return car;
    }

    @Override
    public Car getById(long id) {
        return database.getOrDefault(id, null);
    }

    @Override
    public Car update(Car car) {
        if (car.getId() == null) {
            throw new IllegalArgumentException("Car ID cannot be null for update operation");
        }
        if (!database.containsKey(car.getId())) {
            throw new IllegalArgumentException("Car with ID " + car.getId() + " not found for update");
        }
        database.put(car.getId(), car);
        return car;
    }
    
    @Override
    public boolean delete(long id) {
        if (database.containsKey(id)) {
            database.remove(id);
            return true;
        }
        return false;
    }
}





