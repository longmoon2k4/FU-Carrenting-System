package he186065.fucarrentingsystem.controller;

import he186065.fucarrentingsystem.entity.Account;
import he186065.fucarrentingsystem.entity.Customer;
import he186065.fucarrentingsystem.repository.AccountRepository;
import he186065.fucarrentingsystem.repository.CustomerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final CustomerRepository customers;
    private final AccountRepository accounts;

    public AuthController(CustomerRepository customers, AccountRepository accounts) {
        this.customers = customers;
        this.accounts = accounts;
    }

    // Simple registration endpoint. Expects JSON with email, password and other fields.
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Customer payload) {
        if (payload.getEmail() == null || payload.getPassword() == null) {
            return ResponseEntity.badRequest().body("email and password required");
        }
        // Check duplicates across several fields: email, mobile, identityCard, licenceNumber
        java.util.List<String> dupes = new java.util.ArrayList<>();
        if (payload.getEmail() != null && customers.findByEmail(payload.getEmail()).isPresent()) dupes.add("email");
        if (payload.getMobile() != null && !payload.getMobile().isBlank()) {
            boolean mdup = customers.findAll().stream().anyMatch(c -> payload.getMobile().equals(c.getMobile()));
            if (mdup) dupes.add("mobile");
        }
        if (payload.getIdentityCard() != null && !payload.getIdentityCard().isBlank()) {
            boolean idup = customers.findAll().stream().anyMatch(c -> payload.getIdentityCard().equals(c.getIdentityCard()));
            if (idup) dupes.add("identityCard");
        }
        if (payload.getLicenceNumber() != null && !payload.getLicenceNumber().isBlank()) {
            boolean ldup = customers.findAll().stream().anyMatch(c -> payload.getLicenceNumber().equals(c.getLicenceNumber()));
            if (ldup) dupes.add("licenceNumber");
        }
        if (!dupes.isEmpty()) {
            // return list of duplicated fields so client can show which ones conflict
            return ResponseEntity.status(409).body(String.join(",", dupes));
        }
        // Assign default role via Account. The DB defines two accounts: CUSTOMER (id=1) and ADMIN (id=2).
        // Prefer Account with role 'CUSTOMER', otherwise prefer Account with id 1, otherwise create a CUSTOMER account.
        Account userAccount = accounts.findAll().stream()
                .filter(a -> "CUSTOMER".equalsIgnoreCase(a.getRole()))
                .findFirst()
                .orElseGet(() -> accounts.findById(1).orElseGet(() -> {
                    Account a = new Account();
                    a.setAccountName("customer");
                    a.setRole("CUSTOMER");
                    return accounts.save(a);
                }));
        payload.setAccount(userAccount);
        Customer saved = customers.save(payload);
        return ResponseEntity.created(URI.create("/api/customers/" + saved.getCustomerId())).body(saved);
    }

    // Simple login endpoint. Returns customer object on success.
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody java.util.Map<String,String> body, jakarta.servlet.http.HttpSession session) {
        String email = body.get("email");
        String password = body.get("password");
        if (email == null || password == null) return ResponseEntity.badRequest().body("email and password required");
        java.util.Optional<Customer> byEmail = customers.findByEmail(email);
        if (byEmail.isEmpty()) {
            return ResponseEntity.status(404).body("email_not_found");
        }
        java.util.Optional<Customer> found = customers.findByEmailAndPassword(email, password);
        if (found.isPresent()) {
            // store in session for server-side templates
            session.setAttribute("currentUser", found.get());
            return ResponseEntity.ok(found.get());
        }
        // email exists but password incorrect
        return ResponseEntity.status(401).body("invalid_password");
    }

    // Logout: invalidate server-side session
    @PostMapping("/logout")
    public ResponseEntity<?> logout(jakarta.servlet.http.HttpSession session) {
        try{
            session.invalidate();
        }catch(Exception ignored){}
        return ResponseEntity.ok().build();
    }
}
