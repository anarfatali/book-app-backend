package az.company.bookappbackend.auth.oauth2;

import az.company.bookappbackend.auth.service.JwtService;
import az.company.bookappbackend.common.enums.ReadingFrequency;
import az.company.bookappbackend.common.enums.Role;
import az.company.bookappbackend.common.enums.SubscriptionType;
import az.company.bookappbackend.user.entity.UserEntity;
import az.company.bookappbackend.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class Oauth2Handler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String surname = oAuth2User.getAttribute("family_name");
        String picture = oAuth2User.getAttribute("picture");

        var user = userRepository.findByEmail(email)
                .orElseGet(() -> createUserFromOAuth2(email, name, surname, picture));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(refreshTokenCookie);

        // Redirect to frontend with access token
        String redirectUrl = "/auth/callback?token=" + accessToken;
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private UserEntity createUserFromOAuth2(String email, String name, String surname, String picture) {
        String baseUsername = name + surname;
        String username = baseUsername;
        int counter = 1;

        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter++;
        }

        var user = UserEntity.builder()
                .email(email)
                .passwordHash("")
                .username(username)
                .name(name != null ? name : "")
                .surname(surname != null ? surname : "")
                .avatarUrl(picture)
                .role(Role.USER)
                .verified(true)
                .subscriptionType(SubscriptionType.FREE)
                .readingFrequency(ReadingFrequency.OCCASIONALLY)
                .build();

        return userRepository.save(user);
    }

}
