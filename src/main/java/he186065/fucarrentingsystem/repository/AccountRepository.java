package he186065.fucarrentingsystem.repository;

import he186065.fucarrentingsystem.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer> {
}
