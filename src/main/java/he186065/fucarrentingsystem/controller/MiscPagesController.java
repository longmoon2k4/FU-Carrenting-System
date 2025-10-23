package he186065.fucarrentingsystem.controller;

import he186065.fucarrentingsystem.entity.Customer;
import he186065.fucarrentingsystem.repository.CarRepository;
import he186065.fucarrentingsystem.entity.Car;
import he186065.fucarrentingsystem.entity.CarProducer;
import he186065.fucarrentingsystem.entity.CarRental;
import he186065.fucarrentingsystem.entity.Review;
import he186065.fucarrentingsystem.repository.ReviewRepository;
import he186065.fucarrentingsystem.repository.CarRentalRepository;
import java.time.LocalDate;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MiscPagesController {

    private final CarRepository carRepository;
    private final CarRentalRepository carRentalRepository;
    private final ReviewRepository reviewRepository;
    private final he186065.fucarrentingsystem.repository.CarProducerRepository carProducerRepository;

    public MiscPagesController(CarRepository carRepository, CarRentalRepository carRentalRepository, ReviewRepository reviewRepository, he186065.fucarrentingsystem.repository.CarProducerRepository carProducerRepository) {
        this.carRepository = carRepository;
        this.carRentalRepository = carRentalRepository;
        this.reviewRepository = reviewRepository;
        this.carProducerRepository = carProducerRepository;
    }

    @GetMapping("/rent")
    public String rent(HttpSession session,
                       Model model,
                       @org.springframework.web.bind.annotation.RequestParam(name = "q", required = false) String q,
                       @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                       @RequestParam(name = "size", required = false, defaultValue = "8") int size,
                       @RequestParam(name = "producerId", required = false) Integer producerId,
                       @RequestParam(name = "color", required = false) String color,
                       @RequestParam(name = "modelYear", required = false) Integer modelYear,
                       @RequestParam(name = "capacity", required = false) Integer capacity,
                       @RequestParam(name = "minPrice", required = false) java.math.BigDecimal minPrice,
                       @RequestParam(name = "maxPrice", required = false) java.math.BigDecimal maxPrice){
        Object o = session.getAttribute("currentUser");
        if(o instanceof Customer) model.addAttribute("currentUser", (Customer)o);
        // pageable fetch for available cars
        if (page < 0) page = 0;
        if (size <= 0) size = 8;
        Pageable pageable = PageRequest.of(page, size);
        Page<Car> carPage;
        // populate producer list for filter dropdown
        java.util.List<CarProducer> producers = carProducerRepository.findAll();
        model.addAttribute("producers", producers);

        if((q != null && !q.trim().isEmpty()) || producerId != null || (color != null && !color.trim().isEmpty()) || modelYear != null || capacity != null || minPrice != null || maxPrice != null){
            String tq = (q == null) ? null : q.trim();
            carPage = carRepository.searchAvailableWithFilters(tq, "AVAILABLE", producerId, (color == null || color.trim().isEmpty()) ? null : color.trim(), modelYear, capacity, minPrice, maxPrice, pageable);
            model.addAttribute("q", tq != null ? tq : "");
        } else {
            carPage = carRepository.findByStatusIgnoreCase("AVAILABLE", pageable);
            model.addAttribute("q", "");
        }
        model.addAttribute("producerId", producerId);
        model.addAttribute("color", color);
        model.addAttribute("modelYear", modelYear);
        model.addAttribute("capacity", capacity);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("carPage", carPage);
        model.addAttribute("availableCars", carPage.getContent());
        model.addAttribute("currentPage", carPage.getNumber());
        model.addAttribute("totalPages", carPage.getTotalPages());
        model.addAttribute("pageSize", carPage.getSize());
        return "rent";
    }

    @GetMapping("/return")
    public String ret(HttpSession session, Model model){
        Object o = session.getAttribute("currentUser");
        if(!(o instanceof Customer)){
            model.addAttribute("errorMessage","Vui lòng đăng nhập để trả xe.");
            return "return";
        }
        Customer cust = (Customer)o;
        model.addAttribute("currentUser", cust);

        // find active rentals for this customer
        java.util.List<CarRental> active = carRentalRepository.findByCustomerCustomerIdAndStatusIgnoreCase(cust.getCustomerId(), "RENTED");
        model.addAttribute("activeRentals", active);
        return "return";
    }

    @org.springframework.web.bind.annotation.PostMapping("/return/{rentalId}")
    public String processReturn(@org.springframework.web.bind.annotation.PathVariable Integer rentalId,
                                @org.springframework.web.bind.annotation.RequestParam(name = "star", required = false) Byte star,
                                @org.springframework.web.bind.annotation.RequestParam(name = "comment", required = false) String comment,
                                HttpSession session,
                                org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs){
        Object o = session.getAttribute("currentUser");
        if(!(o instanceof Customer)){
            redirectAttrs.addFlashAttribute("errorMessage","Vui lòng đăng nhập để trả xe.");
            return "redirect:/return";
        }
        Customer cust = (Customer)o;

        CarRental rental = carRentalRepository.findById(rentalId).orElse(null);
        if(rental == null || rental.getCustomer() == null || !cust.getCustomerId().equals(rental.getCustomer().getCustomerId())){
            redirectAttrs.addFlashAttribute("errorMessage","Giao dịch không tìm thấy hoặc không thuộc về bạn.");
            return "redirect:/return";
        }

        if(!"RENTED".equalsIgnoreCase(rental.getStatus())){
            redirectAttrs.addFlashAttribute("errorMessage","Giao dịch không ở trạng thái thuê.");
            return "redirect:/return";
        }

        // mark returned
        rental.setStatus("RETURNED");
        carRentalRepository.save(rental);

        // update car status to AVAILABLE
        if(rental.getCar() != null){
            Car car = rental.getCar();
            car.setStatus("AVAILABLE");
            carRepository.save(car);
        }

        // optionally store review
        if(star != null && star >= 1 && star <= 5 && comment != null && !comment.trim().isEmpty()){
            Review rev = new Review();
            rev.setCar(rental.getCar());
            rev.setCustomer(cust);
            rev.setReviewStar(star);
            rev.setComment(comment.trim());
            reviewRepository.save(rev);
        }

        redirectAttrs.addFlashAttribute("successMessage","Trả xe thành công.");
        return "redirect:/return";
    }

    // @GetMapping("/wallet")
    // public String wallet(HttpSession session, Model model){
    //     Object o = session.getAttribute("currentUser");
    //     if(o instanceof Customer) model.addAttribute("currentUser", (Customer)o);
    //     return "wallet";
    // }

    @GetMapping("/support")
    public String support(HttpSession session, Model model){
        Object o = session.getAttribute("currentUser");
        if(o instanceof Customer) model.addAttribute("currentUser", (Customer)o);
        return "support";
    }

    @GetMapping("/transactions")
    public String transactions(HttpSession session, Model model,
                               @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                               @RequestParam(name = "size", required = false, defaultValue = "10") int size){
        Object o = session.getAttribute("currentUser");
        if(!(o instanceof Customer)){
            model.addAttribute("errorMessage","Vui lòng đăng nhập để xem lịch sử giao dịch.");
            return "transactions"; // template will show prompt
        }
        Customer cust = (Customer)o;
        model.addAttribute("currentUser", cust);

        if(page < 0) page = 0;
        if(size <= 0) size = 10;

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<he186065.fucarrentingsystem.entity.CarRental> rentPage = carRentalRepository.findByCustomerIdPaged(cust.getCustomerId(), pageable);

        model.addAttribute("rentPage", rentPage);
        model.addAttribute("rentals", rentPage.getContent());
        model.addAttribute("currentPage", rentPage.getNumber());
        model.addAttribute("totalPages", rentPage.getTotalPages());
        model.addAttribute("pageSize", rentPage.getSize());
        return "transactions";
    }

    @GetMapping("/rent/book/{id}")
    public String bookCar(@org.springframework.web.bind.annotation.PathVariable Integer id, HttpSession session, Model model){
        Object o = session.getAttribute("currentUser");
        if(o instanceof Customer) model.addAttribute("currentUser", (Customer)o);

        Car car = carRepository.findById(id).orElse(null);
        if(car == null){
            model.addAttribute("errorMessage", "Xe không tồn tại.");
            return "redirect:/rent";
        }

        model.addAttribute("car", car);
        // provide default pickup/return dates for the booking form
        model.addAttribute("defaultPickup", LocalDate.now().toString());
        model.addAttribute("defaultReturn", LocalDate.now().plusDays(1).toString());

        // if not logged in, show car-detail with prompt to login
        if(!(o instanceof Customer)){
            model.addAttribute("errorMessage", "Vui lòng đăng nhập để thuê xe.");
        }

        return "car-detail";
    }

    @org.springframework.web.bind.annotation.PostMapping("/rent/book/{id}")
    public String confirmBooking(@org.springframework.web.bind.annotation.PathVariable Integer id,
                                 @org.springframework.web.bind.annotation.RequestParam("pickupDate") String pickupDateStr,
                                 @org.springframework.web.bind.annotation.RequestParam("returnDate") String returnDateStr,
                                 HttpSession session,
                                 org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs){
        Object o = session.getAttribute("currentUser");
        if(!(o instanceof Customer)){
            redirectAttrs.addFlashAttribute("errorMessage","Vui lòng đăng nhập để thuê xe.");
            return "redirect:/cars/" + id;
        }

        Car car = carRepository.findById(id).orElse(null);
        if(car == null){
            redirectAttrs.addFlashAttribute("errorMessage","Xe không tồn tại.");
            return "redirect:/rent";
        }

        if(!"AVAILABLE".equalsIgnoreCase(car.getStatus())){
            redirectAttrs.addFlashAttribute("errorMessage","Xe hiện không có sẵn để thuê.");
            return "redirect:/cars/" + id;
        }

        java.time.LocalDate pickup;
        java.time.LocalDate ret;
        try{
            pickup = java.time.LocalDate.parse(pickupDateStr);
            ret = java.time.LocalDate.parse(returnDateStr);
        } catch(Exception ex){
            redirectAttrs.addFlashAttribute("errorMessage","Ngày không hợp lệ.");
            return "redirect:/cars/" + id;
        }

        if(!ret.isAfter(pickup)){
            redirectAttrs.addFlashAttribute("errorMessage","Ngày trả phải sau ngày nhận.");
            return "redirect:/cars/" + id;
        }

        long days = java.time.temporal.ChronoUnit.DAYS.between(pickup, ret);
        if(days <= 0){
            redirectAttrs.addFlashAttribute("errorMessage","Khoảng thời gian thuê không hợp lệ.");
            return "redirect:/cars/" + id;
        }

        java.math.BigDecimal total = car.getRentPrice().multiply(java.math.BigDecimal.valueOf(days));

        // check overlapping rentals
        long overlaps = carRentalRepository.countOverlapping(car.getCarId(), pickup, ret);
        if(overlaps > 0){
            redirectAttrs.addFlashAttribute("errorMessage","Khoảng thời gian đã bị đặt trước. Vui lòng chọn ngày khác.");
            return "redirect:/cars/" + id;
        }

        CarRental rental = new CarRental();
        rental.setCar(car);
        rental.setCustomer((Customer)o);
        rental.setPickupDate(pickup);
        rental.setReturnDate(ret);
        rental.setRentPrice(total);
        rental.setStatus("RENTED");

        carRentalRepository.save(rental);

        car.setStatus("RENTED");
        carRepository.save(car);

        redirectAttrs.addFlashAttribute("successMessage","Thuê xe thành công. Tổng: " + total + " VND");
        return "redirect:/cars/" + id;
    }

    // @GetMapping("/transactions")
    // public String transactions(HttpSession session, Model model){
    //     Object o = session.getAttribute("currentUser");
    //     if(o instanceof Customer) model.addAttribute("currentUser", (Customer)o);
    //     return "transactions";
    // }
}
