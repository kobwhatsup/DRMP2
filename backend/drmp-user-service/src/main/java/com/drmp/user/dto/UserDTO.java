package com.drmp.user.dto;

import com.drmp.user.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户数据传输对象
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Data
@Schema(description = "用户信息")
public class UserDTO {
    
    @Schema(description = "用户ID")
    private Long id;
    
    @Schema(description = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,50}$", message = "用户名只能包含字母、数字和下划线")
    private String username;
    
    @Schema(description = "密码", required = true)
    @JsonIgnore
    @NotBlank(message = "密码不能为空", groups = {Create.class})
    @Size(min = 8, max = 50, message = "密码长度必须在8-50字符之间")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", 
             message = "密码必须包含大小写字母、数字和特殊字符")
    private String password;
    
    @Schema(description = "昵称")
    @Size(max = 100, message = "昵称长度不能超过100字符")
    private String nickname;
    
    @Schema(description = "真实姓名", required = true)
    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 100, message = "真实姓名长度不能超过100字符")
    private String realName;
    
    @Schema(description = "邮箱", required = true)
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100字符")
    private String email;
    
    @Schema(description = "手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @Schema(description = "头像URL")
    private String avatar;
    
    @Schema(description = "所属机构ID", required = true)
    @NotNull(message = "所属机构不能为空")
    private Long orgId;
    
    @Schema(description = "所属机构名称")
    private String orgName;
    
    @Schema(description = "所属机构类型")
    private String orgType;
    
    @Schema(description = "用户状态")
    private User.UserStatus status;
    
    @Schema(description = "角色ID列表")
    private List<Long> roleIds;
    
    @Schema(description = "角色列表")
    private List<RoleDTO> roles;
    
    @Schema(description = "权限代码列表")
    private List<String> permissions;
    
    @Schema(description = "最后登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;
    
    @Schema(description = "最后登录IP")
    private String lastLoginIp;
    
    @Schema(description = "登录次数")
    private Integer loginCount;
    
    @Schema(description = "密码更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime passwordUpdateTime;
    
    @Schema(description = "是否启用多因子认证")
    private Boolean mfaEnabled;
    
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    // 验证分组
    public interface Create {}
    public interface Update {}
    public interface ChangePassword {}
}