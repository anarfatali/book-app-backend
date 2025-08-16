package az.company.bookappbackend.auth.service;

import az.company.bookappbackend.auth.dto.request.LoginRequest;
import az.company.bookappbackend.auth.dto.request.RegisterRequest;
import az.company.bookappbackend.auth.dto.response.AuthResponse;
import az.company.bookappbackend.auth.entity.EmailVerification;
import az.company.bookappbackend.auth.entity.RefreshToken;
import az.company.bookappbackend.auth.exception.BadRequestException;
import az.company.bookappbackend.auth.repository.OtpRepository;
import az.company.bookappbackend.auth.repository.RefreshTokenRepository;
import az.company.bookappbackend.user.entity.UserEntity;
import az.company.bookappbackend.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final OtpRepository otpRepo;
    private final RefreshTokenRepository refreshRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final RateLimitService rateLimitService;

    @Transactional
    public void register(RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail())) throw new BadRequestException("Email already taken");
        if (userRepo.existsByUsername(req.getUsername())) throw new BadRequestException("Username already taken");
        if (!isPasswordStrong(req.getPassword())) throw new BadRequestException("Weak password");
        var u = new UserEntity();
        u.setEmail(req.getEmail().toLowerCase());
        u.setUsername(req.getUsername());
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        u.setDob(req.getDob());
        u.setVerified(false);
        userRepo.save(u);
        sendVerification(u);
    }

    public AuthResponse login(LoginRequest req, HttpServletResponse res) {
        UserEntity user = userRepo.findByEmail(req.getEmail().toLowerCase())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash()))
            throw new BadCredentialsException("Invalid credentials");
        if (!user.isVerified()) throw new BadRequestException("Email not verified");
        String access = jwtService.generateAccessToken(user);
        String refreshPlain = jwtService.generateRefreshToken();
        String refreshHash = passwordEncoder.encode(refreshPlain);

        RefreshToken rt = new RefreshToken();
        rt.setUser(user);
        rt.setTokenHash(refreshHash);
        rt.setExpiresAt(LocalDateTime.now().plusDays(jwtService.getRefreshExpiryDays()));
        refreshRepo.save(rt);

        Cookie cookie = new Cookie("refreshToken", refreshPlain);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) Duration.ofDays(jwtService.getRefreshExpiryDays()).getSeconds());
        res.addCookie(cookie);

        return new AuthResponse(access, "bearer");
    }

    public void sendVerification(UserEntity user) {
        rateLimitService.checkSendOtpAllowed(user.getEmail());
        String otp = generateNumericOtp(6);
        String otpHash = passwordEncoder.encode(otp);
        otpRepo.deleteByUser(user);
        VerificationOtp v = new VerificationOtp();
        v.setUser(user);
        v.setOtpHash(otpHash);
        v.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        otpRepo.save(v);
        emailService.sendVerificationEmail(user.getEmail(), otp, v.getExpiresAt());
    }

    public AuthResponse verifyOtp(String email, String otp, HttpServletResponse response) {
        UserEntity user = userRepo.findByEmail(email).orElseThrow(...);
        EmailVerification v = otpRepo.findByUser(user).orElseThrow(...);
        if (v.getExpiresAt().isBefore(LocalDateTime.now())) throw new BadRequestException("OTP expired");
        if (!passwordEncoder.matches(otp, v.getOtpHash())) throw new BadRequestException("Invalid OTP");
        user.setVerified(true);
        userRepo.save(user);
        otpRepo.delete(v);

        // issue tokens same as login
        String access = jwtService.generateAccessToken(user);
        String refreshPlain = jwtService.generateRefreshToken();
        String refreshHash = passwordEncoder.encode(refreshPlain);
        RefreshToken rt = new RefreshToken(...);
        refreshRepo.save(rt);
        Cookie cookie = new Cookie("refreshToken", refreshPlain);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(...);
        response.addCookie(cookie);

        emailService.sendWelcomeEmail(user.getEmail(), user.getUsername());
        return new AuthResponse(access, "bearer");
    }

    public AuthResponse refreshToken(String refreshPlain, HttpServletResponse response) {
        // find refresh token by comparing hash
        // best: store hashed and compare with passwordEncoder.matches
        RefreshToken token = refreshRepo.findAll().stream()
                .filter(rt -> passwordEncoder.matches(refreshPlain, rt.getTokenHash()))
                .findFirst().orElseThrow();
        if (token.isRevoked() || token.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new BadRequestException("Invalid refresh token");
        UserEntity user = token.getUser();
        // rotate: create new refresh token and revoke old
        token.setRevoked(true);
        refreshRepo.save(token);
        String newRefreshPlain = jwtService.generateRefreshToken();
        RefreshToken newRt = new RefreshToken();
        newRt.setUser(user);
        newRt.setTokenHash(passwordEncoder.encode(newRefreshPlain));
        newRt.setExpiresAt(LocalDateTime.now().plusDays(jwtService.getRefreshExpiryDays()));
        refreshRepo.save(newRt);

        Cookie cookie = new Cookie("refreshToken", newRefreshPlain);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(...);
        response.addCookie(cookie);

        String access = jwtService.generateAccessToken(user);
        return new AuthResponse(access, "bearer");
    }

    public void logout(UserEntity user, String refreshPlain, HttpServletResponse response) {
        // revoke matching refresh
        refreshRepo.findAll().stream()
                .filter(rt -> passwordEncoder.matches(refreshPlain, rt.getTokenHash()))
                .forEach(rt -> {
                    rt.setRevoked(true);
                    refreshRepo.save(rt);
                });
        // delete cookie
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private boolean isPasswordStrong(String password) {
        if (password == null) return false;

        String passwordPattern =
                "^(?=.*[0-9])" +
                        "(?=.*[a-z])" +
                        "(?=.*[A-Z])" +
                        "(?=.*[@#$%^&+=!])" +
                        "(?=\\S+$).{8,}$";

        return password.matches(passwordPattern);
    }

    private String generateNumericOtp(int length) {
        if (length <= 0) throw new IllegalArgumentException("Length must be positive");
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append((int) (Math.random() * 10));
        }
        return otp.toString();
    }
}
