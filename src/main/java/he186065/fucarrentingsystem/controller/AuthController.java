package he186065.fucarrentingsystem.controller;

import he186065.fucarrentingsystem.repository.CustomerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final CustomerRepository customerRepository;

    public AuthController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");
        if (email == null || password == null) return ResponseEntity.badRequest().body("email and password required");

    var opt = customerRepository.findAll().stream()
        .filter(c -> email.equals(c.getEmail()) && password.equals(c.getPassword()))
        .findFirst();
    if (opt.isPresent()) {
        var c = opt.get();
        return ResponseEntity.ok(Map.of("customerId", c.getCustomerId(), "customerName", c.getCustomerName()));
    }
    return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }
}
