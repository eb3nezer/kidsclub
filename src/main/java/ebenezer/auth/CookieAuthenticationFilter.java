package ebenezer.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;

import javax.inject.Inject;

public class CookieAuthenticationFilter extends RememberMeAuthenticationFilter {
    @Inject
    public CookieAuthenticationFilter(AuthenticationManager authenticationManager, RememberMeServices rememberMeServices) {
        super(authenticationManager, rememberMeServices);
    }
}
