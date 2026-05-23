package com.settleFlow.modules.identity.service;

import com.settleFlow.modules.identity.dto.request.LoginRequest;
import com.settleFlow.modules.identity.dto.request.RegisterRequest;
import com.settleFlow.modules.identity.dto.response.TokenResponse;
import com.settleFlow.modules.identity.dto.response.UserResponse;
import com.settleFlow.modules.identity.model.User;
import com.settleFlow.modules.identity.repository.UserRepository;
import com.settleFlow.modules.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw BusinessException.conflict("Email already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))  // bcrypt hash
                .role(request.getRole())
                .active(true)
                .build();

        User saved = userRepository.save(user);

        return UserResponse.from(saved);
    }

    public TokenResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> BusinessException.badRequest("Invalid email or password"));

        if (!user.getActive()) {
            throw BusinessException.badRequest("Account is deactivated");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw BusinessException.badRequest("Invalid email or password");
        }

        return buildTokenResponse(user);
    }

    public TokenResponse refreshToken(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken)) {
            throw BusinessException.badRequest("Invalid or expired refresh token");
        }

        Long userId = jwtService.extractUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.badRequest("User not found"));

        return buildTokenResponse(user);
    }

    private TokenResponse buildTokenResponse(User user) {
        String access  = jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String refresh = jwtService.generateRefreshToken(user.getId(), user.getEmail(), user.getRole().name());

        return TokenResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpiration())
                .build();
    }
}