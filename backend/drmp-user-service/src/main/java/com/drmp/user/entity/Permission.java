package com.drmp.user.entity;

import com.drmp.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限实体类
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Data
@Entity
@Table(name = "permissions")
@EqualsAndHashCode(callSuper = true)
public class Permission extends BaseEntity {
    
    /**
     * 权限名称
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    /**
     * 权限编码
     */
    @Column(name = "code", nullable = false, unique = true, length = 100)
    private String code;
    
    /**
     * 权限类型：菜单/按钮/API
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PermissionType type;
    
    /**
     * 父权限ID
     */
    @Column(name = "parent_id")
    private Long parentId = 0L;
    
    /**
     * 菜单路径或API路径
     */
    @Column(name = "path", length = 255)
    private String path;
    
    /**
     * HTTP方法
     */
    @Column(name = "method", length = 10)
    private String method;
    
    /**
     * 图标
     */
    @Column(name = "icon", length = 100)
    private String icon;
    
    /**
     * 排序序号
     */
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    /**
     * 权限描述
     */
    @Column(name = "description", length = 255)
    private String description;
    
    /**
     * 权限类型枚举
     */
    public enum PermissionType {
        MENU("菜单"),
        BUTTON("按钮"),
        API("API");
        
        private final String description;
        
        PermissionType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 业务方法
    
    /**
     * 是否为菜单权限
     */
    public boolean isMenu() {
        return PermissionType.MENU.equals(this.type);
    }
    
    /**
     * 是否为按钮权限
     */
    public boolean isButton() {
        return PermissionType.BUTTON.equals(this.type);
    }
    
    /**
     * 是否为API权限
     */
    public boolean isApi() {
        return PermissionType.API.equals(this.type);
    }
    
    /**
     * 是否为根权限
     */
    public boolean isRoot() {
        return parentId == null || parentId == 0L;
    }
}