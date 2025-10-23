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

    public AdminController(CustomerRepository customerRepo, CarRepository carRepo, CarRentalRepository rentalRepo, CarProducerRepository producerRepo){
        this.customerRepo = customerRepo;
        this.carRepo = carRepo;
        this.rentalRepo = rentalRepo;
        this.producerRepo = producerRepo;
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

    @PostMapping("/customers/{id}/edit")
    public String updateCustomer(@PathVariable Integer id, @ModelAttribute Customer payload){
        customerRepo.findById(id).ifPresent(existing -> {
            existing.setCustomerName(payload.getCustomerName());
            existing.setEmail(payload.getEmail());
            existing.setMobile(payload.getMobile());
            existing.setIdentityCard(payload.getIdentityCard());
            existing.setLicenceNumber(payload.getLicenceNumber());
            existing.setBirthday(payload.getBirthday());
            existing.setLicenceDate(payload.getLicenceDate());
            customerRepo.save(existing);
        });
        return "redirect:/admin/customers";
    }

    @PostMapping("/customers/{id}/delete")
    public String deleteCustomer(@PathVariable Integer id){
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

    @PostMapping("/cars/{id}/edit")
    public String updateCar(@PathVariable Integer id, @ModelAttribute Car payload){
        carRepo.findById(id).ifPresent(existing -> {
            existing.setCarName(payload.getCarName());
            existing.setCarModelYear(payload.getCarModelYear());
            existing.setColor(payload.getColor());
            existing.setCapacity(payload.getCapacity());
            existing.setDescription(payload.getDescription());
            existing.setRentPrice(payload.getRentPrice());
            existing.setStatus(payload.getStatus());
            carRepo.save(existing);
        });
        return "redirect:/admin/cars";
    }

    @PostMapping("/cars/{id}/delete")
    public String deleteCar(@PathVariable Integer id){
        long count = rentalRepo.countByCarCarId(id);
        if(count > 0){
            carRepo.findById(id).ifPresent(c -> { c.setStatus("INACTIVE"); carRepo.save(c);});
        } else {
            carRepo.deleteById(id);
        }
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
