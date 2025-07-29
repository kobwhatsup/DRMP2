package com.drmp.user.entity;

import com.drmp.common.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户实体类
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Data
@Entity
@Table(name = "users")
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    
    /**
     * 用户名
     */
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;
    
    /**
     * 密码（加密存储）
     */
    @JsonIgnore
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    
    /**
     * 昵称
     */
    @Column(name = "nickname", length = 100)
    private String nickname;
    
    /**
     * 真实姓名
     */
    @Column(name = "real_name", length = 100)
    private String realName;
    
    /**
     * 邮箱
     */
    @Column(name = "email", unique = true, length = 100)
    private String email;
    
    /**
     * 手机号
     */
    @Column(name = "phone", length = 50)
    private String phone;
    
    /**
     * 头像URL
     */
    @Column(name = "avatar", length = 500)
    private String avatar;
    
    /**
     * 所属机构ID
     */
    @Column(name = "org_id", nullable = false)
    private Long orgId;
    
    /**
     * 状态：活跃/禁用/锁定
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status = UserStatus.ACTIVE;
    
    /**
     * 最后登录时间
     */
    @Column(name = "last_login_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;
    
    /**
     * 最后登录IP
     */
    @Column(name = "last_login_ip", length = 50)
    private String lastLoginIp;
    
    /**
     * 登录次数
     */
    @Column(name = "login_count")
    private Integer loginCount = 0;
    
    /**
     * 密码更新时间
     */
    @Column(name = "password_update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime passwordUpdateTime;
    
    /**
     * 是否启用多因子认证
     */
    @Column(name = "mfa_enabled")
    private Boolean mfaEnabled = false;
    
    /**
     * MFA密钥
     */
    @JsonIgnore
    @Column(name = "mfa_secret", length = 255)
    private String mfaSecret;
    
    // 关联关系
    
    /**
     * 所属机构
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", insertable = false, updatable = false)
    private Organization organization;
    
    /**
     * 用户角色列表
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;
    
    /**
     * 用户状态枚举
     */
    public enum UserStatus {
        ACTIVE("活跃"),
        DISABLED("禁用"),
        LOCKED("锁定");
        
        private final String description;
        
        UserStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 业务方法
    
    /**
     * 是否为活跃状态
     */
    public boolean isActive() {
        return UserStatus.ACTIVE.equals(this.status);
    }
    
    /**
     * 是否被锁定
     */
    public boolean isLocked() {
        return UserStatus.LOCKED.equals(this.status);
    }
    
    /**
     * 是否被禁用
     */
    public boolean isDisabled() {
        return UserStatus.DISABLED.equals(this.status);
    }
    
    /**
     * 获取所属机构名称
     */
    public String getOrgName() {
        return organization != null ? organization.getName() : null;
    }
    
    /**
     * 获取所属机构类型
     */
    public String getOrgType() {
        return organization != null ? organization.getType().name() : null;
    }
}