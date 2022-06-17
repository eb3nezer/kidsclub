package kc.ebenezer.auth;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FacebookAuthenticationSuccessHandler extends CustomAuthenticationSuccessHandler {
    @Override
    String getCredentialSource() {
        return "Facebook";
    }

    @Override
    String getPictureUrl(Map<String, Object> details) {
        if (details.get("picture") != null && details.get("picture") instanceof Map) {
            Map<String, Object> picture = (Map<String, Object>) details.get("picture");
            if (picture.get("data") != null && picture.get("data") instanceof  Map) {
                Map<String, Object> data = (Map<String, Object>) picture.get("data");
                if (data.get("url") != null) {
                    return data.get("url").toString();
                }
            }
        }

        return "";
    }

    @Override
    String getFirstName(Map<String, Object> details) {
        if (details.get("first_name") != null) {
            return "" + details.get("first_name");
        }

        return null;
    }

    @Override
    String getLastName(Map<String, Object> details) {
        if (details.get("last_name") != null) {
            return "" + details.get("last_name");
        }

        return null;
    }
}
