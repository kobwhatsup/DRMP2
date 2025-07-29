package com.drmp.user.util;

import com.drmp.user.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class JwtUtils {
    
    @Value("${jwt.secret:drmp-platform-jwt-secret-key-2024}")
    private String secret;
    
    @Value("${jwt.access-token-expiration:7200}")
    private Long accessTokenExpiration; // 2小时
    
    @Value("${jwt.refresh-token-expiration:604800}")
    private Long refreshTokenExpiration; // 7天
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    /**
     * 生成访问令牌
     */
    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("orgId", user.getOrgId());
        claims.put("orgType", user.getOrgType());
        claims.put("tokenType", "access");
        
        return generateToken(claims, user.getUsername(), accessTokenExpiration);
    }
    
    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("tokenType", "refresh");
        
        return generateToken(claims, user.getUsername(), refreshTokenExpiration);
    }
    
    /**
     * 生成令牌
     */
    private String generateToken(Map<String, Object> claims, String subject, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }
    
    /**
     * 从令牌中获取用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            log.error("获取用户名失败", e);
            return null;
        }
    }
    
    /**
     * 从令牌中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get("userId", Long.class);
        } catch (Exception e) {
            log.error("获取用户ID失败", e);
            return null;
        }
    }
    
    /**
     * 从令牌中获取机构ID
     */
    public Long getOrgIdFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get("orgId", Long.class);
        } catch (Exception e) {
            log.error("获取机构ID失败", e);
            return null;
        }
    }
    
    /**
     * 从令牌中获取机构类型
     */
    public String getOrgTypeFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get("orgType", String.class);
        } catch (Exception e) {
            log.error("获取机构类型失败", e);
            return null;
        }
    }
    
    /**
     * 获取令牌过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration();
        } catch (Exception e) {
            log.error("获取过期时间失败", e);
            return null;
        }
    }
    
    /**
     * 获取令牌剩余有效期（秒）
     */
    public Long getTokenExpiration(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            if (expiration != null) {
                long remaining = (expiration.getTime() - System.currentTimeMillis()) / 1000;
                return Math.max(remaining, 0);
            }
        } catch (Exception e) {
            log.error("获取令牌剩余有效期失败", e);
        }
        return 0L;
    }
    
    /**
     * 验证令牌
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return !isTokenExpired(claims);
        } catch (ExpiredJwtException e) {
            log.debug("令牌已过期: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.error("不支持的JWT令牌: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.error("无效的JWT令牌: {}", e.getMessage());
            return false;
        } catch (SignatureException e) {
            log.error("JWT签名验证失败: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.error("JWT令牌参数错误: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 从令牌中获取Claims
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * 检查令牌是否过期
     */
    private boolean isTokenExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }
    
    /**
     * 获取访问令牌有效期（秒）
     */
    public Long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }
    
    /**
     * 获取刷新令牌有效期（秒）
     */
    public Long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
    
    /**
     * 检查是否为刷新令牌
     */
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return "refresh".equals(claims.get("tokenType"));
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 检查是否为访问令牌
     */
    public boolean isAccessToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return "access".equals(claims.get("tokenType"));
        } catch (Exception e) {
            return false;
        }
    }
}