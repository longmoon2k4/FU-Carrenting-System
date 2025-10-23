package he186065.fucarrentingsystem.controller;

import he186065.fucarrentingsystem.entity.Car;
import he186065.fucarrentingsystem.entity.CarRental;
import he186065.fucarrentingsystem.entity.Customer;
import he186065.fucarrentingsystem.repository.CarProducerRepository;
import he186065.fucarrentingsystem.repository.CarRentalRepository;
import he186065.fucarrentingsystem.repository.CarRepository;
import he186065.fucarrentingsystem.repository.CustomerRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final CustomerRepository customerRepo;
    private final CarRepository carRepo;
    private final CarRentalRepository rentalRepo;
    private final CarProducerRepository producerRepo;
    private final he186065.fucarrentingsystem.repository.AccountRepository accountRepo;
    private final he186065.fucarrentingsystem.repository.ReviewRepository reviewRepo;

    public AdminController(CustomerRepository customerRepo, CarRepository carRepo, CarRentalRepository rentalRepo, CarProducerRepository producerRepo, he186065.fucarrentingsystem.repository.AccountRepository accountRepo, he186065.fucarrentingsystem.repository.ReviewRepository reviewRepo){
        this.customerRepo = customerRepo;
        this.carRepo = carRepo;
        this.rentalRepo = rentalRepo;
        this.producerRepo = producerRepo;
        this.accountRepo = accountRepo;
        this.reviewRepo = reviewRepo;
    }

    @GetMapping
    public String dashboard(){ return "admin/dashboard"; }

    // Customers
    @GetMapping("/customers")
    public String customers(Model m){ m.addAttribute("customers", customerRepo.findAll()); return "admin/customers"; }

    @GetMapping("/customers/{id}/edit")
    public String editCustomer(@PathVariable Integer id, Model m){
        Customer c = customerRepo.findById(id).orElse(null);
        m.addAttribute("customer", c);
        return "admin/customer-edit";
    }

    @GetMapping("/customers/new")
    public String newCustomer(Model m){
        // provide empty Customer to reuse edit template
        Customer c = new Customer();
        m.addAttribute("customer", c);
        return "admin/customer-edit";
    }

    @PostMapping("/customers/{id}/edit")
    public String updateCustomer(@PathVariable Integer id,
                                 @ModelAttribute Customer payload,
                                 @RequestParam(required = false) String accountRole,
                                 @RequestParam(required = false) String password,
                                 Model m){
        // Ensure payload knows the id (helps when we re-render the form after validation errors)
        if(payload.getCustomerId() == null){
            payload.setCustomerId(id);
        }

        // Basic validation: email and mobile format
        java.util.List<String> errors = new java.util.ArrayList<>();
        if(payload.getEmail() == null || !payload.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")){
            errors.add("email_invalid");
        }
        if(payload.getMobile() != null && !payload.getMobile().isBlank()){
            // simple phone check: digits, optional +, length 7-15
            if(!payload.getMobile().matches("^\\+?[0-9\\- ]{7,15}$")){
                errors.add("mobile_invalid");
            }
        }

        // uniqueness checks (exclude current record) and collect conflicting owners
        java.util.List<String> dupes = new java.util.ArrayList<>();
        java.util.Map<String, Customer> duplicateOwners = new java.util.HashMap<>();
    customerRepo.findByIdentityCard(payload.getIdentityCard()).ifPresent(c -> { if(!java.util.Objects.equals(c.getCustomerId(), id)){ dupes.add("identityCard"); duplicateOwners.put("identityCard", c); } });
    customerRepo.findByLicenceNumber(payload.getLicenceNumber()).ifPresent(c -> { if(!java.util.Objects.equals(c.getCustomerId(), id)){ dupes.add("licenceNumber"); duplicateOwners.put("licenceNumber", c); } });
        // also check email and mobile duplicates
    customerRepo.findByEmail(payload.getEmail()).ifPresent(c -> { if(!java.util.Objects.equals(c.getCustomerId(), id)){ dupes.add("email"); duplicateOwners.put("email", c); } });
        if(payload.getMobile() != null && !payload.getMobile().isBlank()){
            customerRepo.findByMobile(payload.getMobile()).ifPresent(c -> { if(!java.util.Objects.equals(c.getCustomerId(), id)){ dupes.add("mobile"); duplicateOwners.put("mobile", c); } });
        }

        if(!errors.isEmpty() || !dupes.isEmpty()){
            // return to edit form with messages
            // Keep submitted payload so admin can correct values; also provide duplicate owner details
            m.addAttribute("customer", payload);
            m.addAttribute("validationErrors", errors);
            m.addAttribute("duplicateFields", dupes);
            m.addAttribute("duplicateOwners", duplicateOwners);
            return "admin/customer-edit";
        }

        // persist changes
        customerRepo.findById(id).ifPresent(existing -> {
            existing.setCustomerName(payload.getCustomerName());
            existing.setEmail(payload.getEmail());
            existing.setMobile(payload.getMobile());
            existing.setIdentityCard(payload.getIdentityCard());
            existing.setLicenceNumber(payload.getLicenceNumber());
            existing.setBirthday(payload.getBirthday());
            existing.setLicenceDate(payload.getLicenceDate());
            // update password only if provided (simple plaintext as project uses)
            if(password != null && !password.isBlank()){
                existing.setPassword(password);
            }
            // update account role
            if(accountRole != null && !accountRole.isBlank()){
                he186065.fucarrentingsystem.entity.Account acct = accountRepo.findAll().stream()
                        .filter(a -> accountRole.equalsIgnoreCase(a.getRole()))
                        .findFirst().orElseGet(() -> {
                            he186065.fucarrentingsystem.entity.Account a = new he186065.fucarrentingsystem.entity.Account();
                            a.setAccountName(accountRole.toLowerCase());
                            a.setRole(accountRole.toUpperCase());
                            return accountRepo.save(a);
                        });
                existing.setAccount(acct);
            }

            customerRepo.save(existing);
        });
        return "redirect:/admin/customers";
    }

    @PostMapping("/customers/new")
    public String createCustomer(@ModelAttribute Customer payload,
                                 @RequestParam(required = false) String accountRole,
                                 @RequestParam(required = false) String password,
                                 Model m){
        // Basic validation: email and mobile format
        java.util.List<String> errors = new java.util.ArrayList<>();
        if(payload.getEmail() == null || !payload.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")){
            errors.add("email_invalid");
        }
        if(payload.getMobile() != null && !payload.getMobile().isBlank()){
            if(!payload.getMobile().matches("^\\+?[0-9\\- ]{7,15}$")){
                errors.add("mobile_invalid");
            }
        }

        // uniqueness checks for new record
        java.util.List<String> dupes = new java.util.ArrayList<>();
        java.util.Map<String, Customer> duplicateOwners = new java.util.HashMap<>();
        customerRepo.findByIdentityCard(payload.getIdentityCard()).ifPresent(c -> { dupes.add("identityCard"); duplicateOwners.put("identityCard", c); });
        customerRepo.findByLicenceNumber(payload.getLicenceNumber()).ifPresent(c -> { dupes.add("licenceNumber"); duplicateOwners.put("licenceNumber", c); });
        customerRepo.findByEmail(payload.getEmail()).ifPresent(c -> { dupes.add("email"); duplicateOwners.put("email", c); });
        if(payload.getMobile() != null && !payload.getMobile().isBlank()){
            customerRepo.findByMobile(payload.getMobile()).ifPresent(c -> { dupes.add("mobile"); duplicateOwners.put("mobile", c); });
        }

        if(!errors.isEmpty() || !dupes.isEmpty()){
            m.addAttribute("customer", payload);
            m.addAttribute("validationErrors", errors);
            m.addAttribute("duplicateFields", dupes);
            m.addAttribute("duplicateOwners", duplicateOwners);
            return "admin/customer-edit";
        }

        // set account (create if necessary)
        if(accountRole != null && !accountRole.isBlank()){
            he186065.fucarrentingsystem.entity.Account acct = accountRepo.findAll().stream()
                    .filter(a -> accountRole.equalsIgnoreCase(a.getRole()))
                    .findFirst().orElseGet(() -> {
                        he186065.fucarrentingsystem.entity.Account a = new he186065.fucarrentingsystem.entity.Account();
                        a.setAccountName(accountRole.toLowerCase());
                        a.setRole(accountRole.toUpperCase());
                        return accountRepo.save(a);
                    });
            payload.setAccount(acct);
        }

        // ensure password (if not set, set a default temporary password)
        if(password != null && !password.isBlank()){
            payload.setPassword(password);
        } else if(payload.getPassword() == null || payload.getPassword().isBlank()){
            payload.setPassword("changeme");
        }

        customerRepo.save(payload);
        return "redirect:/admin/customers";
    }

    @PostMapping("/customers/{id}/delete")
    @org.springframework.transaction.annotation.Transactional
    public String deleteCustomer(@PathVariable Integer id){
        // delete related entities first to avoid FK constraint violations
        rentalRepo.deleteByCustomerCustomerId(id);
        reviewRepo.deleteByCustomerCustomerId(id);

        // finally delete the customer itself
        customerRepo.deleteById(id);
        return "redirect:/admin/customers";
    }

    // Cars
    @GetMapping("/cars")
    public String cars(Model m){ m.addAttribute("cars", carRepo.findAll()); return "admin/cars"; }

    @GetMapping("/cars/{id}/edit")
    public String editCar(@PathVariable Integer id, Model m){
        Car c = carRepo.findById(id).orElse(null);
        m.addAttribute("car", c);
        m.addAttribute("producers", producerRepo.findAll());
        return "admin/car-edit";
    }

    @GetMapping("/cars/new")
    public String newCar(Model m){
        Car c = new Car();
        m.addAttribute("car", c);
        m.addAttribute("producers", producerRepo.findAll());
        return "admin/car-edit";
    }

    @PostMapping("/cars/{id}/edit")
    public String updateCar(@PathVariable Integer id, @ModelAttribute Car payload, @RequestParam(value = "imageFile", required = false) org.springframework.web.multipart.MultipartFile imageFile){
        carRepo.findById(id).ifPresent(existing -> {
            existing.setCarName(payload.getCarName());
            existing.setCarModelYear(payload.getCarModelYear());
            existing.setColor(payload.getColor());
            existing.setCapacity(payload.getCapacity());
            existing.setDescription(payload.getDescription());
            existing.setRentPrice(payload.getRentPrice());
            existing.setStatus(payload.getStatus());
            existing.setImageUrl(payload.getImageUrl());
            if(payload.getProducer() != null && payload.getProducer().getProducerId() != null){
                producerRepo.findById(payload.getProducer().getProducerId()).ifPresent(existing::setProducer);
            }
            if(payload.getImportDate() != null){
                existing.setImportDate(payload.getImportDate());
            }
            // handle uploaded image file
            if(imageFile != null && !imageFile.isEmpty()){
                try{
                    java.nio.file.Path imagesDir = java.nio.file.Paths.get("src/main/resources/static/img/cars");
                    java.nio.file.Files.createDirectories(imagesDir);
                    String original = imageFile.getOriginalFilename();
                    String ext = "";
                    if(original != null && original.contains(".")){
                        ext = original.substring(original.lastIndexOf('.'));
                    }
                    String fn = "car_" + System.currentTimeMillis() + ext;
                    java.nio.file.Path dst = imagesDir.resolve(fn);
                    try(java.io.InputStream in = imageFile.getInputStream()){
                        java.nio.file.Files.copy(in, dst, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    }
                    existing.setImageUrl("/img/cars/" + fn);
                }catch(Exception ex){
                    // ignore failure to save image, keep existing imageUrl
                }
            }
            carRepo.save(existing);
        });
        return "redirect:/admin/cars";
    }

    @PostMapping("/cars/new")
    public String createCar(@ModelAttribute Car payload, @RequestParam(required = false) Integer producerId, @RequestParam(value = "imageFile", required = false) org.springframework.web.multipart.MultipartFile imageFile){
        if(producerId != null){
            producerRepo.findById(producerId).ifPresent(payload::setProducer);
        }
        if(payload.getImportDate() == null){
            payload.setImportDate(java.time.LocalDate.now());
        }
        if(payload.getStatus() == null || payload.getStatus().isBlank()){
            payload.setStatus("AVAILABLE");
        }
        // handle uploaded image file
        if(imageFile != null && !imageFile.isEmpty()){
            try{
                java.nio.file.Path imagesDir = java.nio.file.Paths.get("src/main/resources/static/img/cars");
                java.nio.file.Files.createDirectories(imagesDir);
                String original = imageFile.getOriginalFilename();
                String ext = "";
                if(original != null && original.contains(".")){
                    ext = original.substring(original.lastIndexOf('.'));
                }
                String fn = "car_" + System.currentTimeMillis() + ext;
                java.nio.file.Path dst = imagesDir.resolve(fn);
                try(java.io.InputStream in = imageFile.getInputStream()){
                    java.nio.file.Files.copy(in, dst, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
                payload.setImageUrl("/img/cars/" + fn);
            }catch(Exception ex){
                // ignore
            }
        }
        carRepo.save(payload);
        return "redirect:/admin/cars";
    }

    @PostMapping("/cars/{id}/delete")
    public String deleteCar(@PathVariable Integer id, org.springframework.web.servlet.mvc.support.RedirectAttributes ra){
        var opt = carRepo.findById(id);
        if(opt.isEmpty()){
            ra.addFlashAttribute("deleteError", "Xe không tồn tại.");
            return "redirect:/admin/cars";
        }
        var car = opt.get();
        // Only allow deleting when status is AVAILABLE
        if(car.getStatus() == null || !"AVAILABLE".equalsIgnoreCase(car.getStatus())){
            ra.addFlashAttribute("deleteError", "Xe chỉ có thể xoá khi trạng thái là AVAILABLE.");
            return "redirect:/admin/cars";
        }
        long count = rentalRepo.countByCarCarId(id);
        if(count > 0){
            // has rental history -> cannot delete
            ra.addFlashAttribute("deleteError", "Xe có lịch sử thuê, không thể xoá. Nếu muốn tạm ẩn, đặt trạng thái INACTIVE.");
            return "redirect:/admin/cars";
        }

        carRepo.deleteById(id);
        return "redirect:/admin/cars";
    }

    // Rentals management
    @GetMapping("/rentals")
    public String rentals(Model m){ m.addAttribute("rentals", rentalRepo.findAll()); return "admin/rentals"; }

    @PostMapping("/rentals/{id}/status")
    public String updateRentalStatus(@PathVariable Integer id, @RequestParam String status){
        rentalRepo.findById(id).ifPresent(r -> { r.setStatus(status); rentalRepo.save(r); });
        return "redirect:/admin/rentals";
    }

    @PostMapping("/rentals/{id}/delete")
    public String deleteRental(@PathVariable Integer id){ rentalRepo.deleteById(id); return "redirect:/admin/rentals"; }

    // Reports (server-rendered + CSV export)
    @GetMapping("/reports")
    public String reports(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
                          Model m){
        if(start != null && end != null){
            List<CarRental> list = rentalRepo.findByPickupDateBetweenOrderByPickupDateDesc(start, end);
            m.addAttribute("items", list);
            m.addAttribute("count", list.size());
            BigDecimal total = list.stream().map(r -> r.getRentPrice() == null ? BigDecimal.ZERO : r.getRentPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);
            m.addAttribute("total", total);
            m.addAttribute("start", start);
            m.addAttribute("end", end);
        }
        return "admin/reports";
    }

    @GetMapping("/reports/csv")
    public void reportsCsv(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
                           HttpServletResponse resp) throws IOException {
        List<CarRental> list = rentalRepo.findByPickupDateBetweenOrderByPickupDateDesc(start, end);
        resp.setContentType("text/csv;charset=UTF-8");
        String fn = String.format("rentals_%s_%s.csv", start, end);
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + fn + "\"");
        StringBuilder sb = new StringBuilder();
        sb.append("RentalId,CarId,CarName,CustomerId,CustomerName,PickupDate,ReturnDate,RentPrice\n");
        for(var r : list){
            sb.append(r.getCarRentalId()).append(',')
              .append(r.getCar().getCarId()).append(',')
              .append('"').append(r.getCar().getCarName().replaceAll("\"","''")).append('"').append(',')
              .append(r.getCustomer().getCustomerId()).append(',')
              .append('"').append(r.getCustomer().getCustomerName().replaceAll("\"","''")).append('"').append(',')
              .append(r.getPickupDate()).append(',').append(r.getReturnDate()).append(',')
              .append(r.getRentPrice()).append('\n');
        }
        resp.getWriter().write(sb.toString());
    }

}
