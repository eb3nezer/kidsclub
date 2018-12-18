package kc.ebenezer.auth;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TwitterAuthenticationSuccessHandler extends CustomAuthenticationSuccessHandler {
    @Override
    String getCredentialSource() {
        return "Twitter";
    }

    @Override
    String getPictureUrl(Map<String, Object> details) {
        return details.get("picture").toString();
    }

    @Override
    String getFirstName(Map<String, Object> details) {
        return details.get("given_name").toString();
    }

    @Override
    String getLastName(Map<String, Object> details) {
        return details.get("family_name").toString();
    }
}
