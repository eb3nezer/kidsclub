package kc.ebenezer.auth;

import org.springframework.stereotype.Component;

@Component
public class FacebookAuthenticationSuccessHandler extends CustomAuthenticationSuccessHandler {
    @Override
    String getCredentialSource() {
        return "Facebook";
    }
}
