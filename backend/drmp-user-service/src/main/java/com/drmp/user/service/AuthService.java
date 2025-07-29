package com.drmp.user.service;

import com.drmp.user.dto.LoginRequest;
import com.drmp.user.dto.LoginResponse;

/**
 * 认证服务接口
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
public interface AuthService {
    
    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest loginRequest);
    
    /**
     * 用户登出
     *
     * @param token 访问令牌
     */
    void logout(String token);
    
    /**
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return 新的令牌信息
     */
    LoginResponse refreshToken(String refreshToken);
    
    /**
     * 验证令牌
     *
     * @param token 访问令牌
     * @return 是否有效
     */
    boolean validateToken(String token);
    
    /**
     * 从令牌中获取用户ID
     *
     * @param token 访问令牌
     * @return 用户ID
     */
    Long getUserIdFromToken(String token);
    
    /**
     * 从令牌中获取用户名
     *
     * @param token 访问令牌
     * @return 用户名
     */
    String getUsernameFromToken(String token);
}