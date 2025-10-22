package he186065.fucarrentingsystem.controller;

import he186065.fucarrentingsystem.entity.CarRental;
import he186065.fucarrentingsystem.repository.CarRentalRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class CarRentalController {
    private final CarRentalRepository repo;
    public CarRentalController(CarRentalRepository repo) { this.repo = repo; }

    @GetMapping
    public List<CarRental> list() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<CarRental> get(@PathVariable Integer id) {
        return repo.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public CarRental create(@RequestBody CarRental r) { return repo.save(r); }

    @PutMapping("/{id}")
    public ResponseEntity<CarRental> update(@PathVariable Integer id, @RequestBody CarRental r) {
        return repo.findById(id).map(existing -> {
            existing.setCustomer(r.getCustomer());
            existing.setCar(r.getCar());
            existing.setPickupDate(r.getPickupDate());
            existing.setReturnDate(r.getReturnDate());
            existing.setRentPrice(r.getRentPrice());
            existing.setStatus(r.getStatus());
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
