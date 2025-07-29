package com.drmp.user.entity;

import com.drmp.common.entity.BaseEntity;
import com.drmp.common.enums.OrganizationType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 机构实体类
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Data
@Entity
@Table(name = "organizations")
@EqualsAndHashCode(callSuper = true)
public class Organization extends BaseEntity {
    
    /**
     * 机构名称
     */
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    /**
     * 机构类型：SOURCE-案源机构，DISPOSAL-处置机构
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private OrganizationType type;
    
    /**
     * 子类型：银行/消金/网贷/律所/调解中心等
     */
    @Column(name = "sub_type", length = 50)
    private String subType;
    
    /**
     * 状态：待审核/活跃/暂停/拒绝
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrganizationStatus status = OrganizationStatus.PENDING;
    
    /**
     * 联系人
     */
    @Column(name = "contact_person", length = 100)
    private String contactPerson;
    
    /**
     * 联系电话（加密存储）
     */
    @Column(name = "contact_phone", length = 255)
    private String contactPhone;
    
    /**
     * 联系邮箱
     */
    @Column(name = "contact_email", length = 100)
    private String contactEmail;
    
    /**
     * 办公地址
     */
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    
    /**
     * 营业执照文件路径
     */
    @Column(name = "business_license", length = 500)
    private String businessLicense;
    
    /**
     * 法定代表人
     */
    @Column(name = "legal_person", length = 100)
    private String legalPerson;
    
    /**
     * 统一社会信用代码
     */
    @Column(name = "unified_credit_code", length = 50)
    private String unifiedCreditCode;
    
    /**
     * 注册资本（万元）
     */
    @Column(name = "registration_capital", precision = 15, scale = 2)
    private BigDecimal registrationCapital;
    
    /**
     * 成立日期
     */
    @Column(name = "establish_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate establishDate;
    
    // 处置机构特有字段
    
    /**
     * 团队规模（人数）
     */
    @Column(name = "team_size")
    private Integer teamSize;
    
    /**
     * 月处理案件能力
     */
    @Column(name = "monthly_capacity")
    private Integer monthlyCapacity;
    
    /**
     * 当前负载：LOW/MEDIUM/HIGH
     */
    @Column(name = "current_load", length = 50)
    private String currentLoad;
    
    /**
     * 服务区域列表（JSON格式）
     */
    @Column(name = "service_regions", columnDefinition = "JSON")
    @Convert(converter = StringListConverter.class)
    private List<String> serviceRegions;
    
    /**
     * 业务范围（JSON格式）
     */
    @Column(name = "business_scope", columnDefinition = "JSON")
    @Convert(converter = StringListConverter.class)
    private List<String> businessScope;
    
    /**
     * 处置类型：调解/诉讼/催收等（JSON格式）
     */
    @Column(name = "disposal_types", columnDefinition = "JSON")
    @Convert(converter = StringListConverter.class)
    private List<String> disposalTypes;
    
    /**
     * 结算方式：全风险/半风险/无风险（JSON格式）
     */
    @Column(name = "settlement_methods", columnDefinition = "JSON")
    @Convert(converter = StringListConverter.class)
    private List<String> settlementMethods;
    
    /**
     * 合作案例描述
     */
    @Column(name = "cooperation_cases", columnDefinition = "TEXT")
    private String cooperationCases;
    
    /**
     * 机构描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    // 审核相关字段
    
    /**
     * 审核状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "audit_status")
    private AuditStatus auditStatus = AuditStatus.PENDING;
    
    /**
     * 审核意见
     */
    @Column(name = "audit_comment", columnDefinition = "TEXT")
    private String auditComment;
    
    /**
     * 审核时间
     */
    @Column(name = "audit_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditTime;
    
    /**
     * 审核人ID
     */
    @Column(name = "audit_by")
    private Long auditBy;
    
    // 合同相关字段
    
    /**
     * 合同开始日期
     */
    @Column(name = "contract_start_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate contractStartDate;
    
    /**
     * 合同结束日期
     */
    @Column(name = "contract_end_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate contractEndDate;
    
    /**
     * 合同文件路径
     */
    @Column(name = "contract_file", length = 500)
    private String contractFile;
    
    /**
     * 机构状态枚举
     */
    public enum OrganizationStatus {
        PENDING("待审核"),
        ACTIVE("活跃"),
        SUSPENDED("暂停"),
        REJECTED("拒绝");
        
        private final String description;
        
        OrganizationStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 审核状态枚举
     */
    public enum AuditStatus {
        PENDING("待审核"),
        APPROVED("已通过"),
        REJECTED("已拒绝");
        
        private final String description;
        
        AuditStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}