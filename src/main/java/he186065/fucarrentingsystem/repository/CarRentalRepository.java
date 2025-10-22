package he186065.fucarrentingsystem.repository;

import he186065.fucarrentingsystem.entity.CarRental;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRentalRepository extends JpaRepository<CarRental, Integer> {
}
