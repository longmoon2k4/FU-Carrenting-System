package he186065.fucarrentingsystem.controller;

import he186065.fucarrentingsystem.entity.Customer;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("currentUserEmail")
    public String currentUserEmail(HttpSession session){
        try{
            Object o = session.getAttribute("currentUser");
            if(o instanceof Customer){ return ((Customer)o).getEmail(); }
        }catch(Exception ignored){}
        return null;
    }

    @ModelAttribute("currentUserName")
    public String currentUserName(HttpSession session){
        try{
            Object o = session.getAttribute("currentUser");
            if(o instanceof Customer){ return ((Customer)o).getCustomerName(); }
        }catch(Exception ignored){}
        return null;
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin(HttpSession session){
        try{
            Object o = session.getAttribute("currentUser");
            if(o instanceof Customer){
                Customer c = (Customer)o;
                if(c.getAccount() != null && c.getAccount().getRole() != null){
                    return "ADMIN".equalsIgnoreCase(c.getAccount().getRole());
                }
            }
        }catch(Exception ignored){}
        return false;
    }
}
