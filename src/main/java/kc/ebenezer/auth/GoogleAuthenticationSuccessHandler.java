package kc.ebenezer.auth;

import org.springframework.stereotype.Component;

@Component
public class GoogleAuthenticationSuccessHandler extends CustomAuthenticationSuccessHandler {
    @Override
    String getCredentialSource() {
        return "Google";
    }
}
