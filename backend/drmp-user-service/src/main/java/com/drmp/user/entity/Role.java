package com.drmp.user.entity;

import com.drmp.common.entity.BaseEntity;
import com.drmp.common.enums.OrganizationType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 角色实体类
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Data
@Entity
@Table(name = "roles")
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseEntity {
    
    /**
     * 角色名称
     */
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    
    /**
     * 角色编码
     */
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;
    
    /**
     * 角色描述
     */
    @Column(name = "description", length = 255)
    private String description;
    
    /**
     * 适用机构类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "org_type")
    private OrgType orgType;
    
    /**
     * 是否默认角色
     */
    @Column(name = "is_default")
    private Boolean isDefault = false;
    
    /**
     * 排序序号
     */
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    // 关联关系
    
    /**
     * 角色权限列表
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private List<Permission> permissions;
    
    /**
     * 适用机构类型枚举
     */
    public enum OrgType {
        SOURCE("案源机构"),
        DISPOSAL("处置机构"),
        PLATFORM("平台");
        
        private final String description;
        
        OrgType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 业务方法
    
    /**
     * 是否包含指定权限
     */
    public boolean hasPermission(String permissionCode) {
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }
        return permissions.stream()
                .anyMatch(permission -> permission.getCode().equals(permissionCode));
    }
    
    /**
     * 获取所有权限代码
     */
    public List<String> getPermissionCodes() {
        if (permissions == null || permissions.isEmpty()) {
            return List.of();
        }
        return permissions.stream()
                .map(Permission::getCode)
                .toList();
    }
}