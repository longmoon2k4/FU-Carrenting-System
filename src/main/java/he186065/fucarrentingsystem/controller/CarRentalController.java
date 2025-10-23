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
    private final he186065.fucarrentingsystem.repository.CarRepository carRepo;

    public CarRentalController(CarRentalRepository repo, he186065.fucarrentingsystem.repository.CarRepository carRepo) {
        this.repo = repo;
        this.carRepo = carRepo;
    }

    @GetMapping
    public List<CarRental> list() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<CarRental> get(@PathVariable Integer id) {
        return repo.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CarRental r) {
        // Validate car reference
        if (r.getCar() == null || r.getCar().getCarId() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Car id is required to create a rental."));
        }

        Integer carId = r.getCar().getCarId();
        var carOpt = carRepo.findById(carId);
        if (carOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Car not found."));
        }
        var car = carOpt.get();
        // Only allow renting when car is AVAILABLE
        if (!"AVAILABLE".equalsIgnoreCase(car.getStatus())) {
            return ResponseEntity.status(409).body(Map.of("error", "Xe hiện không thể thuê."));
        }

        // Validate dates
        if (r.getPickupDate() == null || r.getReturnDate() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Ngày nhận và ngày trả là bắt buộc."));
        }
        if (r.getPickupDate().isAfter(r.getReturnDate())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Ngày nhận không được sau ngày trả."));
        }

        // Check overlapping rentals for the same car
        long overlaps = repo.countOverlapping(carId, r.getPickupDate(), r.getReturnDate());
        if (overlaps > 0) {
            // Vietnamese message requested by user
            return ResponseEntity.status(409).body(Map.of("error", "Khoảng thời gian đã bị đặt trước. Vui lòng chọn ngày khác."));
        }

        // Ensure rental uses the managed car entity
        r.setCar(car);
        if (r.getStatus() == null) r.setStatus("RENTED");

        CarRental saved = repo.save(r);

        // Mark car as RENTED and persist
        car.setStatus("RENTED");
        carRepo.save(car);

        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody CarRental r) {
        return repo.findById(id).map(existing -> {
            existing.setCustomer(r.getCustomer());
            existing.setCar(r.getCar());
            existing.setPickupDate(r.getPickupDate());
            existing.setReturnDate(r.getReturnDate());
            existing.setRentPrice(r.getRentPrice());
            String oldStatus = existing.getStatus();
            String newStatus = r.getStatus();

            // Resolve managed car entity if a car id was provided
            if (existing.getCar() == null || existing.getCar().getCarId() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Car id is required."));
            }
            Integer carId = existing.getCar().getCarId();
            var carOpt = carRepo.findById(carId);
            if (carOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "Car not found."));
            }
            var car = carOpt.get();
            existing.setCar(car);

            // Validate dates
            if (existing.getPickupDate() == null || existing.getReturnDate() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Ngày nhận và ngày trả là bắt buộc."));
            }
            if (existing.getPickupDate().isAfter(existing.getReturnDate())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Ngày nhận không được sau ngày trả."));
            }

            // If rental will be or remains RENTED, check for overlapping rentals (exclude this rental)
            boolean willBeRented = newStatus != null && "RENTED".equalsIgnoreCase(newStatus);
            // Only check overlaps when the rental will be RENTED (or remains RENTED after update).
            // Do NOT block returning a rental because of overlaps.
            if (willBeRented) {
                long overlaps = repo.countOverlappingExcluding(carId, existing.getPickupDate(), existing.getReturnDate(), existing.getCarRentalId());
                if (overlaps > 0) {
                    return ResponseEntity.status(409).body(Map.of("error", "Khoảng thời gian đã bị đặt trước. Vui lòng chọn ngày khác."));
                }
            }

            // Apply status change and persist
            existing.setStatus(newStatus);
            repo.save(existing);

            // If status changed, update car accordingly
            if(existing.getCar() != null && newStatus != null){
                if("RENTED".equalsIgnoreCase(newStatus)){
                    // Only allow transition to RENTED when car is AVAILABLE
                    if (!"AVAILABLE".equalsIgnoreCase(car.getStatus())) {
                        return ResponseEntity.status(409).body(Map.of("error", "Xe hiện không thể thuê."));
                    }
                    car.setStatus("RENTED");
                    carRepo.save(car);
                } else if("RETURNED".equalsIgnoreCase(newStatus)){
                    car.setStatus("AVAILABLE");
                    carRepo.save(car);
                } else if(oldStatus != null && !oldStatus.equalsIgnoreCase(newStatus) && !"RENTED".equalsIgnoreCase(newStatus)){
                    // For other transitions away from RENTED, ensure AVAILABLE
                    car.setStatus("AVAILABLE");
                    carRepo.save(car);
                }
            }

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
