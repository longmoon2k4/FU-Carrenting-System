package he186065.fucarrentingsystem.config;

import he186065.fucarrentingsystem.entity.Customer;
import he186065.fucarrentingsystem.entity.Account;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AdminRequiredInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if(session == null){
            response.sendRedirect("/");
            return false;
        }
        Object o = session.getAttribute("currentUser");
        if(o instanceof Customer){
            Customer c = (Customer)o;
            Account a = c.getAccount();
            if(a != null && "ADMIN".equalsIgnoreCase(a.getRole())){
                return true;
            }
        }
        // not admin: redirect to home
        response.sendRedirect("/");
        return false;
    }
}
