package he186065.fucarrentingsystem.controller;

import he186065.fucarrentingsystem.repository.CarRentalRepository;
import he186065.fucarrentingsystem.dto.RentalReportItem;
import he186065.fucarrentingsystem.entity.CarRental;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
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

    /**
     * Report rentals between start and end dates (inclusive).
     * Example: /api/rentals/report?start=2025-01-01&end=2025-06-30
     */
    @GetMapping("/report")
    public ResponseEntity<Map<String, Object>> report(@RequestParam String start, @RequestParam String end){
        LocalDate s = LocalDate.parse(start);
        LocalDate e = LocalDate.parse(end);
        java.util.List<CarRental> rentals = repo.findByPickupDateBetweenOrderByPickupDateDesc(s, e);
        java.util.List<RentalReportItem> items = rentals.stream().map(r -> {
            RentalReportItem it = new RentalReportItem();
            it.setRentalId(r.getCarRentalId());
            it.setCarId(r.getCar().getCarId());
            it.setCarName(r.getCar().getCarName());
            it.setCustomerId(r.getCustomer().getCustomerId());
            it.setCustomerName(r.getCustomer().getCustomerName());
            it.setPickupDate(r.getPickupDate());
            it.setReturnDate(r.getReturnDate());
            it.setRentPrice(r.getRentPrice());
            return it;
        }).collect(Collectors.toList());
        Map<String,Object> out = new HashMap<>();
        out.put("count", items.size());
        out.put("total", items.stream().map(i -> i.getRentPrice() == null ? java.math.BigDecimal.ZERO : i.getRentPrice()).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        out.put("items", items);
        return ResponseEntity.ok(out);
    }
}
