package he186065.fucarrentingsystem.repository;

import he186065.fucarrentingsystem.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;

public interface CarRepository extends JpaRepository<Car, Integer> {
	// find cars by status (case-insensitive)
	List<Car> findByStatusIgnoreCase(String status);

	// pageable variant for pagination
	Page<Car> findByStatusIgnoreCase(String status, Pageable pageable);

	// Search available cars by name or producer (case-insensitive) with pagination
	@EntityGraph(attributePaths = {"producer"})
	@Query("SELECT c FROM Car c WHERE LOWER(c.status) = LOWER(:status) AND (LOWER(c.carName) LIKE CONCAT('%', LOWER(:q), '%') OR LOWER(c.producer.producerName) LIKE CONCAT('%', LOWER(:q), '%'))")
	Page<Car> searchAvailableByNameOrProducer(@Param("q") String q, @Param("status") String status, Pageable pageable);

    @EntityGraph(attributePaths = {"producer"})
    @Query("SELECT c FROM Car c WHERE LOWER(c.status) = LOWER(:status)"
	    + " AND (:producerId IS NULL OR c.producer.producerId = :producerId)"
	    + " AND (:color IS NULL OR LOWER(c.color) LIKE CONCAT('%', LOWER(:color), '%'))"
	    + " AND (:modelYear IS NULL OR c.carModelYear = :modelYear)"
	    + " AND (:capacity IS NULL OR c.capacity = :capacity)"
	    + " AND (:minPrice IS NULL OR c.rentPrice >= :minPrice)"
	    + " AND (:maxPrice IS NULL OR c.rentPrice <= :maxPrice)"
	    + " AND (:q IS NULL OR (LOWER(c.carName) LIKE CONCAT('%', LOWER(:q), '%') OR LOWER(c.producer.producerName) LIKE CONCAT('%', LOWER(:q), '%')))"
    )
    Page<Car> searchAvailableWithFilters(@Param("q") String q,
					 @Param("status") String status,
					 @Param("producerId") Integer producerId,
					 @Param("color") String color,
					 @Param("modelYear") Integer modelYear,
					 @Param("capacity") Integer capacity,
					 @Param("minPrice") java.math.BigDecimal minPrice,
					 @Param("maxPrice") java.math.BigDecimal maxPrice,
					 Pageable pageable);
}
