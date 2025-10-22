package he186065.fucarrentingsystem.repository;

import he186065.fucarrentingsystem.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Integer> {
}
