package he186065.fucarrentingsystem.controller;

import he186065.fucarrentingsystem.entity.Car;
import he186065.fucarrentingsystem.repository.CarRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {
    private final CarRepository repo;
    public CarController(CarRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Car> list() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Car> get(@PathVariable Integer id) {
        return repo.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Car create(@RequestBody Car c) { return repo.save(c); }

    @PutMapping("/{id}")
    public ResponseEntity<Car> update(@PathVariable Integer id, @RequestBody Car c) {
        return repo.findById(id).map(existing -> {
            existing.setCarName(c.getCarName());
            existing.setCarModelYear(c.getCarModelYear());
            existing.setColor(c.getColor());
            existing.setCapacity(c.getCapacity());
            existing.setDescription(c.getDescription());
            existing.setImportDate(c.getImportDate());
            existing.setProducer(c.getProducer());
            existing.setRentPrice(c.getRentPrice());
            existing.setStatus(c.getStatus());
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
