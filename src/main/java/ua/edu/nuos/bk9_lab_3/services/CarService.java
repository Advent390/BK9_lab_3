package ua.edu.nuos.bk9_lab_3.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import ua.edu.nuos.bk9_lab_3.data.Car;
import java.util.List;

@Named
@ApplicationScoped
public class CarService {

    @PersistenceContext
    private EntityManager em;

    @Getter
    @Setter
    private Car car = new Car();

    @Getter
    @Setter
    private Car editingCar;

    public Car getCarById(int carId) {
        return em.find(Car.class, carId);
    }

    public void edit(int carId) {
        editingCar = getCarById(carId);
    }

    public List<Car> getAllCars() {
        return em
                .createQuery("SELECT c FROM Car c ORDER BY c.id", Car.class)
                .getResultList();
    }

    @Transactional
    public void createCar() {
        if (isValid(car)) {
            em.persist(car);
            car = new Car();
        } else {
            throw new IllegalStateException();
        }
    }

    @Transactional
    public void updateCar() {
        if (editingCar != null && isValid(editingCar)) {
            em.merge(editingCar);
        } else {
            throw new IllegalStateException();
        }
    }

    @Transactional
    public void deleteCar(int carId) {
        Car carToDelete = getCarById(carId);

        if (carToDelete != null) {
            em.remove(carToDelete);
        }
    }

    // пробіг і ціна не можуть бути від'ємними, дата виробництва - не в майбутньому
    private boolean isValid(Car c) {
        return c.getMileage() != null && c.getMileage() >= 0
                && c.getPrice() != null && c.getPrice() >= 0
                && c.getProductionDate() != null
                && !c.getProductionDate().isAfter(java.time.LocalDate.now());
    }
}