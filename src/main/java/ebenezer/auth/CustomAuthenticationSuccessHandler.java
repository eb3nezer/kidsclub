package ebenezer.auth;

import ebenezer.dao.UserSitePermissionDao;
import ebenezer.dto.UserDetailsDto;
import ebenezer.model.AuditLevel;
import ebenezer.model.User;
import ebenezer.model.UserSitePermission;
import ebenezer.permissions.SitePermission;
import ebenezer.service.AuditService;
import ebenezer.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public abstract class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final Logger LOG = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserSitePermissionDao userSitePermissionDao;

    @Autowired
    private AuditService auditService;

    @Value(value = "${kidsclub.admin}")
    private String adminEmail;

    private KidsClubRememberMeServices kidsClubRememberMeServices;

    abstract String getCredentialSource();

    public void setKidsClubRememberMeServices(KidsClubRememberMeServices kidsClubRememberMeServices) {
        this.kidsClubRememberMeServices = kidsClubRememberMeServices;
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof OAuth2Authentication) {
            boolean newUser = false;

            Map<String, String> details = (Map<String, String>) ((OAuth2Authentication) authentication).getUserAuthentication().getDetails();
            String email = details.get("email");
            // If we didn't get an email address, then we can't do anything
            if (email != null) {
                User user = null;
                // Search by email. Do we already know about this user?
                Optional<User> userResult = userService.getUserByEmail(email);
                if (userResult.isPresent()) {
                    if (!userResult.get().getActive()) {
                        auditService.audit("Denying access to disabled user email=\"" + email + "\"", new Date(), AuditLevel.DEBUG);
                    } else {
                        user = userResult.get();
                        LOG.info("Found existing user id=" + user.getId());
                        // TODO If the user account is not active, consider logging them out again
                        user.setRemoteCredentialSource(getCredentialSource());
                        user.setRemoteCredential(authentication.getName());
                        user.setUpdated(System.currentTimeMillis());
                    }
                } else {
                    auditService.audit("Denying access to unknown user email=\"" + email + "\"", new Date(), AuditLevel.DEBUG);
                    LOG.info("User with email " + email + " does not already exist");
                }

                if (user == null || !user.getLoggedIn()) {
                    String displayName = details.get("name");
                    String firstName = details.get("given_name");
                    String lastName = details.get("family_name");
                    String avatarUrl = details.get("picture");
                    if (user == null) {
                        user = new User(
                                null,
                                displayName,
                                firstName,
                                lastName,
                                email,
                                null,
                                null,
                                Boolean.TRUE,
                                Boolean.TRUE,
                                avatarUrl,
                                null,
                                getCredentialSource(),
                                authentication.getName());
                        userService.createUser(user);
                        auditService.audit("Creating new user on login email=\"" + email + "\"", new Date());
                        LOG.info("Created new user for email " + email + ". id=" + user.getId());
                    } else {
                        user.setName(displayName);
                        user.setGivenName(firstName);
                        user.setFamilyName(lastName);
                        user.setAvatarUrl(avatarUrl);
                        user.setUpdated(new Date());
                        user.setLoggedIn(Boolean.TRUE);
                        auditService.audit("Updating user details after first login for email=\"" + email + "\"", new Date());
                    }
                    newUser = true;
                }

                // Put the UserDetailsDto object in the security context. This will be used to find out who
                // the logged in user is
                UserDetailsDto userDetails = new UserDetailsDto(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRemoteCredential(),
                        user.getRemoteCredentialSource(),
                        user.getActive());
                Authentication newAuthentication = new KidsClubAuthentication(userDetails, null);
                SecurityContextHolder.getContext().setAuthentication(newAuthentication);

                if (email.equalsIgnoreCase(adminEmail)) {
                    // Make sure admin has all permissions
                    Set<SitePermission> existingPermissions = user.getUserSitePermissions()
                            .stream()
                            .map(UserSitePermission::getPermissionKey)
                            .collect(Collectors.toSet());
                    for (SitePermission sitePermission : SitePermission.values()) {
                        if (!existingPermissions.contains(sitePermission)) {
                            auditService.audit("Adding new site permission " + sitePermission.toString() +
                                    " to admin email=\"" + email + "\" at login", new Date());
                            UserSitePermission userSitePermission = new UserSitePermission(user, sitePermission);
                            userSitePermission = userSitePermissionDao.create(userSitePermission);
                            user.getUserSitePermissions().add(userSitePermission);
                        }
                    }
                }

                // Generate a "remember me" cookie so that this user can be logged in again without needing
                // to type their password again.
                kidsClubRememberMeServices.addCookie(request, response, newAuthentication);

                if (newUser) {
                    getRedirectStrategy().sendRedirect(request, response, getBaseUrl(request) + "/secure/new_user.html");
                    return;
                }
            }
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private static String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String host = request.getServerName();
        int port = request.getServerPort();
        String contextPath = request.getContextPath();

        String baseUrl = scheme + "://" + host + ((("http".equals(scheme) && port == 80) || ("https".equals(scheme) && port == 443)) ? "" : ":" + port) + contextPath;
        return baseUrl;
    }
}
