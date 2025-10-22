package he186065.fucarrentingsystem.controller;

import he186065.fucarrentingsystem.entity.Review;
import he186065.fucarrentingsystem.repository.ReviewRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewRepository repo;
    public ReviewController(ReviewRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Review> list() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Review> get(@PathVariable Integer id) {
        return repo.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Review create(@RequestBody Review r) { return repo.save(r); }

    @PutMapping("/{id}")
    public ResponseEntity<Review> update(@PathVariable Integer id, @RequestBody Review r) {
        return repo.findById(id).map(existing -> {
            existing.setCustomer(r.getCustomer());
            existing.setCar(r.getCar());
            existing.setReviewStar(r.getReviewStar());
            existing.setComment(r.getComment());
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
