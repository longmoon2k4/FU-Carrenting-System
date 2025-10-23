package he186065.fucarrentingsystem.controller;

import he186065.fucarrentingsystem.entity.Customer;
import he186065.fucarrentingsystem.repository.CarRepository;
import he186065.fucarrentingsystem.entity.Car;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MiscPagesController {

    private final CarRepository carRepository;

    public MiscPagesController(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @GetMapping("/rent")
    public String rent(HttpSession session, Model model){
        Object o = session.getAttribute("currentUser");
        if(o instanceof Customer) model.addAttribute("currentUser", (Customer)o);
        // fetch available cars and add to model
        java.util.List<Car> available = carRepository.findByStatusIgnoreCase("AVAILABLE");
        model.addAttribute("availableCars", available);
        return "rent";
    }

    @GetMapping("/return")
    public String ret(HttpSession session, Model model){
        Object o = session.getAttribute("currentUser");
        if(o instanceof Customer) model.addAttribute("currentUser", (Customer)o);
        return "return";
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

    // @GetMapping("/transactions")
    // public String transactions(HttpSession session, Model model){
    //     Object o = session.getAttribute("currentUser");
    //     if(o instanceof Customer) model.addAttribute("currentUser", (Customer)o);
    //     return "transactions";
    // }
}
