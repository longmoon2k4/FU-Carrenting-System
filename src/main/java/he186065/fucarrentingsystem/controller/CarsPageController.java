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
    public String cars(HttpSession session, Model model){
        // page is mostly client-side; we expose currentUser if present so header can render properly
        Object o = session.getAttribute("currentUser");
        if(o!=null){ model.addAttribute("currentUser", o); }
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
