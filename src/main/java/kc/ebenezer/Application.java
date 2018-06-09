package kc.ebenezer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@SpringBootApplication
@Controller
@EnableTransactionManagement
public class Application {
    public static final Long MAX_UPLOAD_SIZE = 5000000L;
    public static final String MAX_UPLOAD_SIZE_DESCRIPTION = "5Mb";

    private Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @RequestMapping({
        "/admin",
        "/admin/admin",
        "/admin/viewproject/**",
        "/admin/editproject/**",
        "/admin/newproject",
        "/admin/audit",
        "/admin/profile",
        "/admin/viewteam/**"
    })
    public String index() {
        return "forward:/admin/index.html";
    }

    @Bean
    public FilterRegistrationBean oauth2ClientFilterRegistration(
            OAuth2ClientContextFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            LOG.info("Starting KC");
        };
    }

}
