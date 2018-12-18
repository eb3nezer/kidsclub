package kc.ebenezer.auth;

import kc.ebenezer.dao.UserSitePermissionDao;
import kc.ebenezer.dto.UserDto;
import kc.ebenezer.dto.mapper.UserMapper;
import kc.ebenezer.model.AuditLevel;
import kc.ebenezer.model.User;
import kc.ebenezer.model.UserSitePermission;
import kc.ebenezer.permissions.SitePermission;
import kc.ebenezer.service.AuditService;
import kc.ebenezer.service.UserService;
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

    @Autowired
    private UserMapper userMapper;

    @Value(value = "${kidsclub.admin}")
    private String adminEmail;

    private KidsClubRememberMeServices kidsClubRememberMeServices;

    abstract String getCredentialSource();

    abstract String getPictureUrl(Map<String, Object> details);

    abstract String getFirstName(Map<String, Object> details);

    abstract String getLastName(Map<String, Object> details);

    public void setKidsClubRememberMeServices(KidsClubRememberMeServices kidsClubRememberMeServices) {
        this.kidsClubRememberMeServices = kidsClubRememberMeServices;
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof OAuth2Authentication) {
            boolean newUser = false;

            Map<String, Object> details = (Map<String, Object>) ((OAuth2Authentication) authentication).getUserAuthentication().getDetails();
            String email = details.get("email").toString();
            // If we didn't get an email address, then we can't do anything
            if (email != null) {
                User user = null;
                // Search by email. Do we already know about this user?
                Optional<User> userResult = userService.getUserByEmail(email);
                if (userResult.isPresent()) {
                    if (!userResult.get().getActive()) {
                        auditService.audit(null, "Denying access to disabled user email=\"" + email + "\"", new Date(), AuditLevel.DEBUG);
                    } else {
                        user = userResult.get();
                        LOG.info("Found existing user id=" + user.getId());
                        // TODO If the user account is not active, consider logging them out again
                        user.setRemoteCredentialSource(getCredentialSource());
                        user.setRemoteCredential(authentication.getName());
                        user.setUpdated(System.currentTimeMillis());
                    }
                } else {
                    auditService.audit(null, "Denying access to unknown user email=\"" + email + "\"", new Date(), AuditLevel.DEBUG);
                    LOG.info("User with email " + email + " does not already exist");
                }

                if (user == null || !user.getLoggedIn()) {
                    String displayName = details.get("name").toString();
                    String firstName = getFirstName(details);
                    String lastName = getLastName(details);
                    String avatarUrl = getPictureUrl(details);
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
                        auditService.audit(null, "Creating new user on login email=\"" + email + "\"", new Date());
                        LOG.info("Created new user for email " + email + ". id=" + user.getId());
                    } else {
                        user.setName(displayName);
                        user.setGivenName(firstName);
                        user.setFamilyName(lastName);
                        user.setAvatarUrl(avatarUrl);
                        user.setUpdated(new Date());
                        user.setLoggedIn(Boolean.TRUE);
                        auditService.audit(null, "Updating user details after first login for email=\"" + email + "\"", new Date());
                    }
                    newUser = true;
                }

                if (email.equalsIgnoreCase(adminEmail)) {
                    // Make sure admin has all permissions
                    Set<SitePermission> existingPermissions = user.getUserSitePermissions()
                        .stream()
                        .map(UserSitePermission::getPermissionKey)
                        .collect(Collectors.toSet());
                    for (SitePermission sitePermission : SitePermission.values()) {
                        if (!existingPermissions.contains(sitePermission)) {
                            auditService.audit(null, "Adding new site permission " + sitePermission.toString() +
                                " to admin email=\"" + email + "\" at login", new Date());
                            UserSitePermission userSitePermission = new UserSitePermission(user, sitePermission);
                            userSitePermission = userSitePermissionDao.create(userSitePermission);
                            user.getUserSitePermissions().add(userSitePermission);
                        }
                    }
                }

                // Put the UserDetailsDto object in the security context. This will be used to find out who
                // the logged in user is
                UserDto userDetails =  userMapper.toDto(user);
                Authentication newAuthentication = new KidsClubAuthentication(userDetails, null);
                SecurityContextHolder.getContext().setAuthentication(newAuthentication);

                // Generate a "remember me" cookie so that this user can be logged in again without needing
                // to type their password again.
                kidsClubRememberMeServices.addCookie(request, response, newAuthentication);

                if (newUser) {
                    getRedirectStrategy().sendRedirect(request, response, getBaseUrl(request) + "/view/profile/5");
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

        return scheme + "://" + host + ((("http".equals(scheme) && port == 80) || ("https".equals(scheme) && port == 443)) ? "" : ":" + port) + contextPath;
    }
}
