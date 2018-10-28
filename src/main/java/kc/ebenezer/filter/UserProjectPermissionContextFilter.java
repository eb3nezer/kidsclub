package kc.ebenezer.filter;

import kc.ebenezer.dao.UserProjectPermissionDao;
import kc.ebenezer.model.User;
import kc.ebenezer.model.UserProjectPermissionContext;
import kc.ebenezer.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.servlet.*;
import java.io.IOException;
import java.util.Optional;

@Component
@Order(1)
public class UserProjectPermissionContextFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Inject
    private UserProjectPermissionDao userProjectPermissionDao;
    @Inject
    private UserService userService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            Optional<User> currentUser = userService.getCurrentUser();
            if (currentUser.isPresent()) {
                UserProjectPermissionContext userProjectPermissionContext = userProjectPermissionDao.getUserProjectPermissionContext(currentUser.get());
                userService.setUserProjectPermissionContext(request, userProjectPermissionContext);
            }
        } catch (Exception e) {
            LOG.error("Failed to set user project permission context", e);
        }

        chain.doFilter(request, response);

        userService.setUserProjectPermissionContext(request, null);
    }

    @Override
    public void destroy() {
    }

    @Bean
    public FilterRegistrationBean<UserProjectPermissionContextFilter> loggingFilter(){
        FilterRegistrationBean<UserProjectPermissionContextFilter> registrationBean
            = new FilterRegistrationBean<>();

        registrationBean.setFilter(new UserProjectPermissionContextFilter());
        registrationBean.addUrlPatterns("/rest/*");

        return registrationBean;
    }
}
