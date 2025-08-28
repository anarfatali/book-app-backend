package az.company.bookappbackend.auth.service;

import az.company.bookappbackend.auth.dto.request.LoginRequest;
import az.company.bookappbackend.auth.dto.request.LogoutRequest;
import az.company.bookappbackend.auth.dto.request.RefreshTokenRequest;
import az.company.bookappbackend.auth.dto.request.RegisterRequest;
import az.company.bookappbackend.auth.dto.request.ResendVerificationRequest;
import az.company.bookappbackend.auth.dto.request.VerificationRequest;
import az.company.bookappbackend.auth.dto.response.AuthResponse;
import az.company.bookappbackend.auth.exception.AccountDeactivatedException;
import az.company.bookappbackend.auth.exception.AlreadyExistsException;
import az.company.bookappbackend.auth.exception.BadCredentialsException;
import az.company.bookappbackend.auth.exception.InvalidOtpException;
import az.company.bookappbackend.auth.exception.InvalidTokenException;
import az.company.bookappbackend.auth.exception.OtpExpiredException;
import az.company.bookappbackend.auth.exception.TokenExpiredException;
import az.company.bookappbackend.auth.exception.UserNotFoundException;
import az.company.bookappbackend.auth.exception.UserNotVerifiedException;
import az.company.bookappbackend.auth.exception.VerificationNotFoundException;
import az.company.bookappbackend.auth.model.EmailVerification;
import az.company.bookappbackend.auth.model.RefreshToken;
import az.company.bookappbackend.auth.repository.EmailVerificationRepository;
import az.company.bookappbackend.auth.repository.RefreshTokenRepository;
import az.company.bookappbackend.common.enums.Role;
import az.company.bookappbackend.user.entity.UserEntity;
import az.company.bookappbackend.user.mapper.UserMapper;
import az.company.bookappbackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final EmailVerificationRepository emailVerificationRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final UserMapper userMapper;
    private final JwtService jwtService;


    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("AuthService::register Email already exists: {}", request.getEmail());
            throw new AlreadyExistsException("Email already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            log.error("AuthService::register Username already exists: {}", request.getUsername());
            throw new AlreadyExistsException("Username already exists");
        }

        var user = UserEntity.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .surname(request.getSurname())
                .birthday(request.getBirthday())
                .subscriptionType(request.getSubscriptionType())
                .readingFrequency(request.getReadingFrequency())
                .interests(request.getInterests())
                .bio(request.getBio())
                .readingExperience(request.getReadingExperience())
                .isPrivate(request.isPrivate())
                .role(Role.USER)
                .verified(false)
                .isDeleted(false)
                .build();

        userRepository.save(user);
        log.info("AuthService::register User registered successfully: {}", user.getUsername());

        sendVerificationEmail(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        var user = findUserByEmailOrUsername(request.getIdentifier());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getIdentifier(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            log.error("AuthService::login Failed to authenticate user: {}", request.getIdentifier(), e);
            throw new BadCredentialsException("Invalid email or password");
        }

        if (!user.isVerified()) {
            log.error("AuthService::login User not verified: {}", user.getEmail());
            throw new UserNotVerifiedException("Email not verified. Please verify your email first.");
        }

        if (user.isDeleted()) {
            log.error("AuthService::login User deleted: {}", user.getEmail());
            throw new AccountDeactivatedException("Account has been deactivated");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        saveRefreshToken(user, refreshToken);

        return AuthResponse.builder()
                .message("Login successful")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(15 * 60L)
                .user(userMapper.toUserDto(user))
                .build();
    }

    @Transactional
    public void verifyEmail(VerificationRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.isVerified()) {
            log.error("AuthService::verifyEmail Email already verified for user: {}", user.getEmail());
            throw new AlreadyExistsException("Email already verified");
        }

        EmailVerification verification = emailVerificationRepository.findByUserAndVerifiedAtIsNull(user)
                .orElseThrow(()
                        -> new VerificationNotFoundException("No pending verification found. Please request a new one"));

        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.error("AuthService::verifyEmail OTP has expired for user: {}", user.getEmail());
            throw new OtpExpiredException("OTP has expired. Please request a new one");
        }

        if (!passwordEncoder.matches(request.getOtp(), verification.getOtpHash())) {
            log.error("AuthService::verifyEmail Invalid OTP for user: {}", user.getEmail());
            throw new InvalidOtpException("Invalid OTP");
        }

        verification.setVerifiedAt(LocalDateTime.now());
        user.setVerified(true);

        emailVerificationRepository.save(verification);
        userRepository.save(user);
        log.info("AuthService::verifyEmail Email verified successfully for user: {}", user.getEmail());

        emailService.sendWelcomeEmail(user);
    }

    @Transactional
    public void resendVerification(ResendVerificationRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.getEmail()));

        if (user.isVerified()) {
            log.error("AuthService::resendVerification Email already verified for user: {}", user.getEmail());
            throw new AlreadyExistsException("Email already verified");
        }

        emailVerificationRepository.deleteByUser(user);

        sendVerificationEmail(user);
        log.info("AuthService::resendVerification Verification email resent to: {}", user.getEmail());
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshTokenValue = request.getRefreshToken();

        RefreshToken refreshToken = refreshTokenRepository.findByTokenHashAndRevokedFalse(refreshTokenValue)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            log.error("AuthService::refreshToken Refresh token expired for user: {}", refreshToken.getUser().getEmail());
            throw new TokenExpiredException("Refresh token expired");
        }

        UserEntity user = refreshToken.getUser();
        String newAccessToken = jwtService.generateAccessToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshTokenValue)
                .expiresIn(15 * 60L)
                .user(userMapper.toUserDto(refreshToken.getUser()))
                .build();
    }

    @Transactional
    public void logout(LogoutRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHashAndRevokedFalse(request.getRefreshToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        log.info("AuthService::logout Refresh token revoked for user: {}", refreshToken.getUser().getEmail());
    }

    private UserEntity findUserByEmailOrUsername(String emailOrUsername) {
        return userRepository.findByEmail(emailOrUsername)
                .orElseGet(() -> userRepository.findByUsername(emailOrUsername)
                        .orElseThrow(() -> new UserNotFoundException("User not found: " + emailOrUsername)));
    }

    private void saveRefreshToken(UserEntity user, String refreshToken) {
        refreshTokenRepository.revokeAllByUser(user);

        RefreshToken token = RefreshToken.builder()
                .user(user)
                .tokenHash(refreshToken)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();

        refreshTokenRepository.save(token);
    }

    private void sendVerificationEmail(UserEntity user) {
        String otp = String.format("%06d", secureRandom.nextInt(1000000));

        EmailVerification verification = EmailVerification.builder()
                .user(user)
                .otpHash(passwordEncoder.encode(otp))
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        emailVerificationRepository.save(verification);

        emailService.sendVerificationEmail(user, otp);
        log.info("Verification email sent to: {}", user.getEmail());
    }
}
