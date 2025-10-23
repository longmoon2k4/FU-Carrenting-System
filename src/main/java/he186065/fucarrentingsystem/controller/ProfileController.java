package he186065.fucarrentingsystem.controller;

import he186065.fucarrentingsystem.entity.Customer;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model){
        Object o = session.getAttribute("currentUser");
        if(o instanceof Customer){
            model.addAttribute("currentUser", (Customer)o);
            return "profile"; // templates/profile.html
        }
        return "redirect:/";
    }
}

