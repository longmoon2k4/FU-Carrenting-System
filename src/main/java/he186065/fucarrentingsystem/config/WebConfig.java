package he186065.fucarrentingsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new LoginRequiredInterceptor())
        .addPathPatterns("/**"); // Chặn tất cả, loại trừ xử lý trong Interceptor
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // Đảm bảo tài nguyên tĩnh được phục vụ
    registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
    registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
    registry.addResourceHandler("/img/**").addResourceLocations("classpath:/static/img/");
    registry.addResourceHandler("/font/**").addResourceLocations("classpath:/static/font/");
    registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
  }
}
