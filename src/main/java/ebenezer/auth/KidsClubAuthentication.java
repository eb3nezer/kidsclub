package ebenezer.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

public class KidsClubAuthentication extends UsernamePasswordAuthenticationToken {
    public KidsClubAuthentication(UserDetails principal, Object credentials) {
        super(principal, credentials, Collections.EMPTY_SET);
    }
}
