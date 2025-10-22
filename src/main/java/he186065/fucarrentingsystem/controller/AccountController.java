package he186065.fucarrentingsystem.controller;

import he186065.fucarrentingsystem.entity.Account;
import he186065.fucarrentingsystem.repository.AccountRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountRepository repo;

    public AccountController(AccountRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Account> list() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Account> get(@PathVariable Integer id) {
        return repo.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Account create(@RequestBody Account a) { return repo.save(a); }

    @PutMapping("/{id}")
    public ResponseEntity<Account> update(@PathVariable Integer id, @RequestBody Account a) {
        return repo.findById(id).map(existing -> {
            existing.setAccountName(a.getAccountName());
            existing.setRole(a.getRole());
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
