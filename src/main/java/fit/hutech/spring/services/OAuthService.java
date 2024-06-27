package fit.hutech.spring.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import fit.hutech.spring.entities.User;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Service
public class OAuthService extends DefaultOAuth2UserService {

    @Autowired
    private UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Get user attributes from OAuth2 provider
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String username = (String) attributes.get("email");

        // Check if the user exists in the database
        User user = userService.findByUsername(username).orElseGet(() -> {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(username);
            String randomPassword = UUID.randomUUID().toString(); // Generate random password
            newUser.setPassword(new BCryptPasswordEncoder().encode(randomPassword)); // Encode random password
            newUser.setProvider("GOOGLE");
            userService.save(newUser);
            userService.setDefaultRole(newUser.getUsername());
            return newUser;
        });

        // Return a DefaultOAuth2User with the necessary authorities
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "email");
    }

    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals(role)) {
                return true;
            }
        }
        return false;
    }
}
