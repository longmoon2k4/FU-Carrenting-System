package he186065.fucarrentingsystem.repository;

import he186065.fucarrentingsystem.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
	java.util.Optional<Customer> findByEmail(String email);
	java.util.Optional<Customer> findByEmailAndPassword(String email, String password);
}
