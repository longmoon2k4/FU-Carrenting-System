package he186065.fucarrentingsystem.repository;

import he186065.fucarrentingsystem.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
	// delete all reviews belonging to a customer
	void deleteByCustomerCustomerId(Integer customerId);
}
