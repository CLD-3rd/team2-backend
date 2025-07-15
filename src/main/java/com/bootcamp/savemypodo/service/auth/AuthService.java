package com.bootcamp.savemypodo.service.auth;

import com.bootcamp.savemypodo.config.jwt.JwtTokenProvider;
import com.bootcamp.savemypodo.config.security.utils.CookieUtil;
import com.bootcamp.savemypodo.entity.User;
import com.bootcamp.savemypodo.global.exception.ErrorCode;
import com.bootcamp.savemypodo.global.exception.UserException;
import com.bootcamp.savemypodo.repository.UserRepository;
import com.bootcamp.savemypodo.service.redis.RedisRefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final CookieUtil cookieUtil;
    private final RedisRefreshTokenService redisRefreshTokenService;

    public Cookie generateRefreshToken(HttpServletRequest request) {

        String refreshToken = cookieUtil.getTokenFromCookie(request, "refreshToken");

        if (refreshToken == null) {
            throw new UserException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new UserException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        if (!refreshToken.equals(redisRefreshTokenService.getRefreshToken(user.getId()))) {
            throw new UserException(ErrorCode.REFRESH_TOKEN_MISMATCH);
        }

        // 🔄 새 AccessToken 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(user);

        return cookieUtil.createCookie("accessToken", newAccessToken);
    }
}
