package he186065.fucarrentingsystem.repository;

import he186065.fucarrentingsystem.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
}
