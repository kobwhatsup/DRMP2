package com.drmp.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 登录请求DTO
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Data
@Schema(description = "登录请求")
public class LoginRequest {
    
    @Schema(description = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名长度不能超过50字符")
    private String username;
    
    @Schema(description = "密码", required = true)
    @NotBlank(message = "密码不能为空")
    @Size(max = 100, message = "密码长度不能超过100字符")
    private String password;
    
    @Schema(description = "验证码")
    private String captcha;
    
    @Schema(description = "验证码键")
    private String captchaKey;
    
    @Schema(description = "是否记住我")
    private Boolean rememberMe = false;
    
    @Schema(description = "客户端IP地址")
    private String clientIp;
    
    @Schema(description = "用户代理")
    private String userAgent;
}