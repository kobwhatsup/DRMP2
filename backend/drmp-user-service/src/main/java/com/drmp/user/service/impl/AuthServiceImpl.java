package com.drmp.user.service.impl;

import com.drmp.common.exception.BusinessException;
import com.drmp.user.dto.LoginRequest;
import com.drmp.user.dto.LoginResponse;
import com.drmp.user.entity.User;
import com.drmp.user.repository.UserRepository;
import com.drmp.user.service.AuthService;
import com.drmp.user.service.UserService;
import com.drmp.user.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";
    private static final String REFRESH_TOKEN_PREFIX = "token:refresh:";
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("用户登录，用户名：{}", loginRequest.getUsername());
        
        // 查找用户
        User user = userRepository.findByUsernameAndDeletedFalse(loginRequest.getUsername())
                .orElseThrow(() -> new BusinessException("用户名或密码错误"));
        
        // 检查用户状态
        if (!user.isActive()) {
            throw new BusinessException("用户已被禁用或锁定");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 检查机构状态
        if (user.getOrganization() != null && 
            user.getOrganization().getStatus() != com.drmp.user.entity.Organization.OrganizationStatus.ACTIVE) {
            throw new BusinessException("所属机构未激活，无法登录");
        }
        
        // 生成令牌
        String accessToken = jwtUtils.generateAccessToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);
        
        // 存储刷新令牌到Redis
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + user.getId(),
                refreshToken,
                jwtUtils.getRefreshTokenExpiration(),
                TimeUnit.SECONDS
        );
        
        // 更新最后登录信息
        userService.updateLastLoginInfo(user.getId(), loginRequest.getClientIp());
        
        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setTokenType("Bearer");
        response.setExpiresIn(jwtUtils.getAccessTokenExpiration());
        
        // 构建用户信息
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setNickname(user.getNickname());
        userInfo.setRealName(user.getRealName());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setEmail(user.getEmail());
        userInfo.setPhone(user.getPhone());
        userInfo.setOrgId(user.getOrgId());
        userInfo.setOrgName(user.getOrgName());
        userInfo.setOrgType(user.getOrgType());
        userInfo.setLastLoginTime(user.getLastLoginTime());
        
        // 设置角色和权限
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            userInfo.setRoles(user.getRoles().stream()
                    .map(role -> role.getCode())
                    .toList());
            
            userInfo.setPermissions(user.getRoles().stream()
                    .flatMap(role -> role.getPermissions().stream())
                    .map(permission -> permission.getCode())
                    .distinct()
                    .toList());
        }
        
        response.setUserInfo(userInfo);
        
        log.info("用户登录成功，用户ID：{}，用户名：{}", user.getId(), user.getUsername());
        
        return response;
    }
    
    @Override
    public void logout(String token) {
        log.info("用户登出");
        
        if (validateToken(token)) {
            // 将令牌加入黑名单
            Long expiration = jwtUtils.getTokenExpiration(token);
            if (expiration > 0) {
                redisTemplate.opsForValue().set(
                        TOKEN_BLACKLIST_PREFIX + token,
                        "blacklisted",
                        expiration,
                        TimeUnit.SECONDS
                );
            }
            
            // 删除刷新令牌
            Long userId = getUserIdFromToken(token);
            if (userId != null) {
                redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);
            }
            
            log.info("用户登出成功，用户ID：{}", userId);
        }
    }
    
    @Override
    public LoginResponse refreshToken(String refreshToken) {
        log.debug("刷新令牌");
        
        if (!jwtUtils.validateToken(refreshToken)) {
            throw new BusinessException("刷新令牌无效");
        }
        
        Long userId = jwtUtils.getUserIdFromToken(refreshToken);
        String storedRefreshToken = (String) redisTemplate.opsForValue()
                .get(REFRESH_TOKEN_PREFIX + userId);
        
        if (!refreshToken.equals(storedRefreshToken)) {
            throw new BusinessException("刷新令牌无效");
        }
        
        // 查找用户
        User user = userRepository.findById(userId)
                .filter(u -> !u.getDeleted() && u.isActive())
                .orElseThrow(() -> new BusinessException("用户不存在或已被禁用"));
        
        // 生成新的访问令牌
        String newAccessToken = jwtUtils.generateAccessToken(user);
        String newRefreshToken = jwtUtils.generateRefreshToken(user);
        
        // 更新刷新令牌
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + userId,
                newRefreshToken,
                jwtUtils.getRefreshTokenExpiration(),
                TimeUnit.SECONDS
        );
        
        LoginResponse response = new LoginResponse();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);
        response.setTokenType("Bearer");
        response.setExpiresIn(jwtUtils.getAccessTokenExpiration());
        
        log.debug("令牌刷新成功，用户ID：{}", userId);
        
        return response;
    }
    
    @Override
    public boolean validateToken(String token) {
        if (!jwtUtils.validateToken(token)) {
            return false;
        }
        
        // 检查令牌是否在黑名单中
        return !Boolean.TRUE.equals(redisTemplate.hasKey(TOKEN_BLACKLIST_PREFIX + token));
    }
    
    @Override
    public Long getUserIdFromToken(String token) {
        return jwtUtils.getUserIdFromToken(token);
    }
    
    @Override
    public String getUsernameFromToken(String token) {
        return jwtUtils.getUsernameFromToken(token);
    }
}