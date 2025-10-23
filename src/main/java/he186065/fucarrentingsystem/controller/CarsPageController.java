package he186065.fucarrentingsystem.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CarsPageController {

    @GetMapping("/cars")
    public String cars(HttpSession session, Model model){
        // page is mostly client-side; we expose currentUser if present so header can render properly
        Object o = session.getAttribute("currentUser");
        if(o!=null){ model.addAttribute("currentUser", o); }
        return "cars";
    }
}
