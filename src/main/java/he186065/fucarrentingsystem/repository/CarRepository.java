package he186065.fucarrentingsystem.repository;

import he186065.fucarrentingsystem.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CarRepository extends JpaRepository<Car, Integer> {
	// find cars by status (case-insensitive)
	List<Car> findByStatusIgnoreCase(String status);
}
