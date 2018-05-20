package ebenezer.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class KidsClubRememberMeServices extends PersistentTokenBasedRememberMeServices {
    public KidsClubRememberMeServices(String key, UserDetailsService userDetailsService, PersistentTokenRepository tokenRepository) {
        super(key, userDetailsService, tokenRepository);
    }

    public void addCookie(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        onLoginSuccess(request, response, authentication);
    }

    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens,
                                                 HttpServletRequest request, HttpServletResponse response) {
        try {
            return super.processAutoLoginCookie(cookieTokens, request, response);
        } catch (CookieTheftException e) {
            try {
                response.sendRedirect("/public/session_error.html");
                return null;
            } catch (IOException e1) {
                // Look, I tried OK?
                throw e;
            }
        }
    }
}
