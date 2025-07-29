package com.drmp.user.controller;

import com.drmp.common.dto.Result;
import com.drmp.user.dto.LoginRequest;
import com.drmp.user.dto.LoginResponse;
import com.drmp.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户登录、登出、令牌刷新等认证相关接口")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户名密码登录，返回访问令牌和刷新令牌")
    public Result<LoginResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {
        
        // 设置客户端信息
        loginRequest.setClientIp(getClientIpAddress(request));
        loginRequest.setUserAgent(request.getHeader("User-Agent"));
        
        LoginResponse response = authService.login(loginRequest);
        
        return Result.success("登录成功", response);
    }
    
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "注销当前用户会话，将令牌加入黑名单")
    public Result<Void> logout(
            @Parameter(description = "访问令牌", required = true)
            @RequestHeader("Authorization") String authorization) {
        
        String token = extractToken(authorization);
        authService.logout(token);
        
        return Result.success("登出成功");
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    public Result<LoginResponse> refreshToken(
            @Parameter(description = "刷新令牌", required = true)
            @RequestParam String refreshToken) {
        
        LoginResponse response = authService.refreshToken(refreshToken);
        
        return Result.success("令牌刷新成功", response);
    }
    
    @PostMapping("/validate")
    @Operation(summary = "验证令牌", description = "验证访问令牌是否有效")
    public Result<Boolean> validateToken(
            @Parameter(description = "访问令牌", required = true)
            @RequestHeader("Authorization") String authorization) {
        
        String token = extractToken(authorization);
        boolean valid = authService.validateToken(token);
        
        return Result.success("令牌验证完成", valid);
    }
    
    /**
     * 提取令牌
     */
    private String extractToken(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        throw new IllegalArgumentException("无效的Authorization头");
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}