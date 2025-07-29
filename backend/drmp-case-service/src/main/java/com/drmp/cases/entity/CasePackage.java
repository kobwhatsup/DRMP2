package com.drmp.cases.entity;

import com.drmp.common.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 案件包实体类
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Data
@Entity
@Table(name = "case_packages")
@EqualsAndHashCode(callSuper = true)
public class CasePackage extends BaseEntity {
    
    /**
     * 案件包名称
     */
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    /**
     * 案件包描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /**
     * 案源机构ID
     */
    @Column(name = "source_org_id", nullable = false)
    private Long sourceOrgId;
    
    /**
     * 案件总数量
     */
    @Column(name = "total_count", nullable = false)
    private Integer totalCount = 0;
    
    /**
     * 案件总金额
     */
    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;
    
    /**
     * 已分案数量
     */
    @Column(name = "assigned_count", nullable = false)
    private Integer assignedCount = 0;
    
    /**
     * 已分案金额
     */
    @Column(name = "assigned_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal assignedAmount = BigDecimal.ZERO;
    
    /**
     * 状态：草稿/已发布/处理中/已完成/已撤回
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CasePackageStatus status = CasePackageStatus.DRAFT;
    
    /**
     * 发布时间
     */
    @Column(name = "publish_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;
    
    /**
     * 期望回款率（%）
     */
    @Column(name = "expected_recovery_rate", precision = 5, scale = 2)
    private BigDecimal expectedRecoveryRate;
    
    /**
     * 期望处置周期（天）
     */
    @Column(name = "expected_period")
    private Integer expectedPeriod;
    
    /**
     * 偏好处置方式（JSON格式）
     */
    @Column(name = "preferred_methods", columnDefinition = "JSON")
    @Convert(converter = StringListConverter.class)
    private List<String> preferredMethods;
    
    /**
     * 分案策略配置（JSON格式）
     */
    @Column(name = "assignment_strategy", columnDefinition = "JSON")
    private String assignmentStrategy;
    
    /**
     * 导入文件路径
     */
    @Column(name = "import_file_path", length = 500)
    private String importFilePath;
    
    /**
     * 导入状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "import_status")
    private ImportStatus importStatus = ImportStatus.PENDING;
    
    /**
     * 导入进度（%）
     */
    @Column(name = "import_progress")
    private Integer importProgress = 0;
    
    /**
     * 导入错误信息
     */
    @Column(name = "import_error_msg", columnDefinition = "TEXT")
    private String importErrorMsg;
    
    // 关联关系
    
    /**
     * 案件列表（一对多关系）
     */
    @OneToMany(mappedBy = "casePackage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Case> cases;
    
    /**
     * 案件包状态枚举
     */
    public enum CasePackageStatus {
        DRAFT("草稿"),
        PUBLISHED("已发布"),
        PROCESSING("处理中"),
        COMPLETED("已完成"),
        WITHDRAWN("已撤回");
        
        private final String description;
        
        CasePackageStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 导入状态枚举
     */
    public enum ImportStatus {
        PENDING("待导入"),
        PROCESSING("导入中"),
        SUCCESS("导入成功"),
        FAILED("导入失败");
        
        private final String description;
        
        ImportStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 业务方法
    
    /**
     * 是否为草稿状态
     */
    public boolean isDraft() {
        return CasePackageStatus.DRAFT.equals(this.status);
    }
    
    /**
     * 是否已发布
     */
    public boolean isPublished() {
        return CasePackageStatus.PUBLISHED.equals(this.status) || 
               CasePackageStatus.PROCESSING.equals(this.status);
    }
    
    /**
     * 是否可以编辑
     */
    public boolean canEdit() {
        return CasePackageStatus.DRAFT.equals(this.status);
    }
    
    /**
     * 是否可以发布
     */
    public boolean canPublish() {
        return CasePackageStatus.DRAFT.equals(this.status) && 
               totalCount > 0 && 
               ImportStatus.SUCCESS.equals(this.importStatus);
    }
    
    /**
     * 获取剩余未分案数量
     */
    public Integer getRemainingCount() {
        return totalCount - assignedCount;
    }
    
    /**
     * 获取剩余未分案金额
     */
    public BigDecimal getRemainingAmount() {
        return totalAmount.subtract(assignedAmount);
    }
    
    /**
     * 获取分案进度百分比
     */
    public Integer getAssignmentProgress() {
        if (totalCount == 0) {
            return 0;
        }
        return (assignedCount * 100) / totalCount;
    }
}