package he186065.fucarrentingsystem.controller;

import he186065.fucarrentingsystem.entity.Customer;
import he186065.fucarrentingsystem.repository.CustomerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerRepository repo;
    public CustomerController(CustomerRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Customer> list() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> get(@PathVariable Integer id) {
        return repo.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Customer create(@RequestBody Customer c) { return repo.save(c); }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> update(@PathVariable Integer id, @RequestBody Customer c) {
        return repo.findById(id).map(existing -> {
            existing.setCustomerName(c.getCustomerName());
            existing.setMobile(c.getMobile());
            existing.setBirthday(c.getBirthday());
            existing.setIdentityCard(c.getIdentityCard());
            existing.setLicenceNumber(c.getLicenceNumber());
            existing.setLicenceDate(c.getLicenceDate());
            existing.setEmail(c.getEmail());
            existing.setPassword(c.getPassword());
            existing.setAccount(c.getAccount());
            repo.save(existing);
            return ResponseEntity.ok(existing);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
