package he186065.fucarrentingsystem.controller;

import he186065.fucarrentingsystem.entity.Customer;
import he186065.fucarrentingsystem.repository.CarRepository;
import he186065.fucarrentingsystem.entity.Car;
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

    public MiscPagesController(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @GetMapping("/rent")
    public String rent(HttpSession session,
                       Model model,
                       @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                       @RequestParam(name = "size", required = false, defaultValue = "8") int size){
        Object o = session.getAttribute("currentUser");
        if(o instanceof Customer) model.addAttribute("currentUser", (Customer)o);
        // pageable fetch for available cars
        if (page < 0) page = 0;
        if (size <= 0) size = 8;
        Pageable pageable = PageRequest.of(page, size);
        Page<Car> carPage = carRepository.findByStatusIgnoreCase("AVAILABLE", pageable);
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
