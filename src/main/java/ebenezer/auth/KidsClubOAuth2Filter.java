package ebenezer.auth;

import ebenezer.dto.UserDetailsDto;
import ebenezer.model.User;
import ebenezer.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.filter.OAuth2AuthenticationFailureEvent;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class KidsClubOAuth2Filter extends OAuth2ClientAuthenticationProcessingFilter {
    private static final Logger LOG = LoggerFactory.getLogger(KidsClubOAuth2Filter.class);

    private UserService userService;
    private String adminEmail;

    public KidsClubOAuth2Filter(String defaultFilterProcessesUrl, UserService userService, String adminEmail) {
        super(defaultFilterProcessesUrl);
        this.userService = userService;
        this.adminEmail = adminEmail;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        Authentication authentication = super.attemptAuthentication(request, response);

        boolean knownUser = false;
        String email = null;

        if ((authentication instanceof OAuth2Authentication) &&
                (((OAuth2Authentication) authentication).getUserAuthentication().getDetails() instanceof Map)) {
            Map<String, String> details = (Map<String, String>) ((OAuth2Authentication) authentication).getUserAuthentication().getDetails();
            email = details.get("email");
            // If we didn't get an email address, then we can't do anything
            if (email != null) {
                if (adminEmail != null && !adminEmail.isEmpty() && email.equalsIgnoreCase(adminEmail)) {
                    // If you have the email address of the system admin, then you are always allowed
                    knownUser = true;
                } else {
                    // Search by email. Do we already know about this user?
                    Optional<User> userResult = userService.getUserByEmail(email);
                    if (userResult.isPresent()) {
                        knownUser = true;
                    }
                }
            }
        }

        if (!knownUser) {
            if (email != null) {
                LOG.error("User with email \"" + email + "\" tried to log in, but this user is unknown");
            } else {
                LOG.error("User with unknown or invalid email address tried to log in");
            }
            throw new DisabledException("User unknown");
        }

        return authentication;
    }
}
