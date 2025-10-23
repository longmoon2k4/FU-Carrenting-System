package he186065.fucarrentingsystem.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginRequiredInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    String uri = request.getRequestURI();

    // Cho phép trang chủ và tài nguyên tĩnh
    if ("/".equals(uri) ||
        uri.startsWith("/css/") ||
        uri.startsWith("/js/") ||
        uri.startsWith("/img/") ||
        uri.startsWith("/font/") ||
        uri.startsWith("/webjars/") ||
        uri.startsWith("/favicon") ||
        uri.startsWith("/error") ||
        // Allow authentication endpoints so AJAX login/register/logout can work
        uri.startsWith("/api/auth")) {
      return true;
    }

    HttpSession session = request.getSession(false);
    Object user = (session != null) ? session.getAttribute("currentUser") : null;
    if (user != null) {
      return true;
    }

    // Chưa login => về trang chủ
    response.sendRedirect("/");
    return false;
  }
}
