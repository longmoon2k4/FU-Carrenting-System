package he186065.fucarrentingsystem.controller;

import he186065.fucarrentingsystem.entity.Car;
import he186065.fucarrentingsystem.repository.CarRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CarsPageController {

    private final CarRepository carRepository;

    public CarsPageController(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @GetMapping("/cars")
    public String cars(HttpSession session, Model model,
                       @org.springframework.web.bind.annotation.RequestParam(name = "q", required = false) String q,
                       @org.springframework.web.bind.annotation.RequestParam(name = "page", required = false, defaultValue = "0") int page,
                       @org.springframework.web.bind.annotation.RequestParam(name = "size", required = false, defaultValue = "8") int size){
        // expose current user so header can render properly
        Object o = session.getAttribute("currentUser");
        if(o!=null){ model.addAttribute("currentUser", o); }

        if(page < 0) page = 0;
        if(size <= 0) size = 8;

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);

        if(q != null && !q.trim().isEmpty()){
            // perform paged search by name or producer
            org.springframework.data.domain.Page<Car> carPage = carRepository.searchAvailableByNameOrProducer(q.trim(), "AVAILABLE", pageable);
            model.addAttribute("carPage", carPage);
            model.addAttribute("availableCars", carPage.getContent());
            model.addAttribute("currentPage", carPage.getNumber());
            model.addAttribute("totalPages", carPage.getTotalPages());
            model.addAttribute("pageSize", carPage.getSize());
            model.addAttribute("q", q.trim());
            return "cars";
        }

        // default: just render cars page (client-side may load data)
        org.springframework.data.domain.Page<Car> carPage = carRepository.findByStatusIgnoreCase("AVAILABLE", pageable);
        model.addAttribute("carPage", carPage);
        model.addAttribute("availableCars", carPage.getContent());
        model.addAttribute("currentPage", carPage.getNumber());
        model.addAttribute("totalPages", carPage.getTotalPages());
        model.addAttribute("pageSize", carPage.getSize());
        return "cars";
    }

    @GetMapping("/cars/{id}")
    public String carDetail(@PathVariable Integer id, HttpSession session, Model model){
        Object o = session.getAttribute("currentUser");
        if(o!=null){ model.addAttribute("currentUser", o); }
        Car car = carRepository.findById(id).orElse(null);
        if(car == null){ return "redirect:/cars"; }
        model.addAttribute("car", car);
        return "car-detail";
    }
}
