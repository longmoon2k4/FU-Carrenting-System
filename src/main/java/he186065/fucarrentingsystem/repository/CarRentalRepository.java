package he186065.fucarrentingsystem.repository;

import he186065.fucarrentingsystem.entity.CarRental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CarRentalRepository extends JpaRepository<CarRental, Integer> {

	@Query("SELECT COUNT(r) FROM CarRental r WHERE r.car.carId = :carId AND NOT (r.returnDate <= :pickup OR r.pickupDate >= :ret)")
	long countOverlapping(@Param("carId") Integer carId, @Param("pickup") java.time.LocalDate pickup, @Param("ret") java.time.LocalDate ret);

	@Query("SELECT r FROM CarRental r WHERE r.customer.customerId = :customerId ORDER BY r.pickupDate DESC")
	Page<CarRental> findByCustomerIdPaged(@Param("customerId") Integer customerId, Pageable pageable);
	List<CarRental> findByCustomerCustomerIdAndStatusIgnoreCase(Integer customerId, String status);

}
