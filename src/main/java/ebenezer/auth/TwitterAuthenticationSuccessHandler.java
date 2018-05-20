package ebenezer.auth;

import org.springframework.stereotype.Component;

@Component
public class TwitterAuthenticationSuccessHandler extends CustomAuthenticationSuccessHandler {
    @Override
    String getCredentialSource() {
        return "Google";
    }
}
