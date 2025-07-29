package com.drmp.common.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类
 * 包含通用字段：主键、创建时间、更新时间、创建人、更新人等
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @LastModifiedDate
    @Column(name = "update_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    /**
     * 创建人ID
     */
    @CreatedBy
    @Column(name = "create_by", updatable = false)
    private Long createBy;
    
    /**
     * 更新人ID
     */
    @LastModifiedBy
    @Column(name = "update_by")
    private Long updateBy;
    
    /**
     * 逻辑删除标记：0-未删除，1-已删除
     */
    @Column(name = "deleted", nullable = false, columnDefinition = "tinyint(1) default 0")
    private Boolean deleted = Boolean.FALSE;
    
    /**
     * 版本号（乐观锁）
     */
    @Version
    @Column(name = "version", nullable = false, columnDefinition = "int default 0")
    private Integer version = 0;
    
    /**
     * 租户ID（多租户支持）
     */
    @Column(name = "tenant_id")
    private Long tenantId;
    
    @PrePersist
    protected void onCreate() {
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
        if (updateTime == null) {
            updateTime = LocalDateTime.now();
        }
        if (deleted == null) {
            deleted = Boolean.FALSE;
        }
        if (version == null) {
            version = 0;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}