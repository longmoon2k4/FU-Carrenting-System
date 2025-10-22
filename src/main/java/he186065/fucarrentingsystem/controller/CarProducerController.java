package he186065.fucarrentingsystem.controller;

import he186065.fucarrentingsystem.entity.CarProducer;
import he186065.fucarrentingsystem.repository.CarProducerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/producers")
public class CarProducerController {
    private final CarProducerRepository repo;
    public CarProducerController(CarProducerRepository repo) { this.repo = repo; }

    @GetMapping
    public List<CarProducer> list() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<CarProducer> get(@PathVariable Integer id) {
        return repo.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public CarProducer create(@RequestBody CarProducer p) { return repo.save(p); }

    @PutMapping("/{id}")
    public ResponseEntity<CarProducer> update(@PathVariable Integer id, @RequestBody CarProducer p) {
        return repo.findById(id).map(existing -> {
            existing.setProducerName(p.getProducerName());
            existing.setAddress(p.getAddress());
            existing.setCountry(p.getCountry());
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
