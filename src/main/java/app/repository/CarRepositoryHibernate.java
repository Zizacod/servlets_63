package app.repository;

import app.model.Car;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class CarRepositoryHibernate implements CarRepository{


    private EntityManager entityManager;

    public CarRepositoryHibernate() {
        entityManager = new Configuration().configure("hibernate/postgres.cfg.xml")
                .buildSessionFactory().createEntityManager();
    }

    @Override
    public List<Car> getAll() {
        return entityManager.createQuery("from Car", Car.class).getResultList();
    }

    @Override
    public Car save(Car car) {

       EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            entityManager.persist(car);

            transaction.commit();
        } catch (Exception e) {

            if (transaction.isActive()) transaction.rollback();
            throw new RuntimeException(e);
        }

        return car;
    }

    @Override
    public Car getById(long id) {

        return entityManager.find(Car.class, id);
    }

    @Override
    public Car update(Car car) throws IllegalArgumentException {
        return null;
    }

    @Override
    public boolean delete(long id) {
        return false;
    }
}
