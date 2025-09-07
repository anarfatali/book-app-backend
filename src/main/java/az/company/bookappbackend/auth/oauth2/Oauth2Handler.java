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
import java.time.LocalDate;

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
        String picture = oAuth2User.getAttribute("picture");
        LocalDate birthDate = oAuth2User.getAttribute("birthday");

        var user = userRepository.findByEmail(email)
                .orElseGet(() -> createUserFromOAuth2(email, name, picture, birthDate));

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

    private UserEntity createUserFromOAuth2(String email, String name, String picture, LocalDate birthDate) {
        String username = name;
        int counter = 1;

        while (userRepository.existsByUsername(username)) {
            username = name + counter++;
        }

        var user = UserEntity.builder()
                .email(email)
                .passwordHash("")
                .username(username)
                .avatarUrl(picture)
                .birthday(birthDate)
                .role(Role.USER)
                .isVerified(true)
                .subscriptionType(SubscriptionType.FREE)
                .readingFrequency(ReadingFrequency.OCCASIONALLY)
                .build();

        return userRepository.save(user);
    }

}
