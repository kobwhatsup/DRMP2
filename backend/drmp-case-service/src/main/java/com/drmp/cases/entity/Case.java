package com.drmp.cases.entity;

import com.drmp.common.entity.BaseEntity;
import com.drmp.common.enums.CaseStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 案件实体类
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Data
@Entity
@Table(name = "cases_template") // 实际使用时按月分表
@EqualsAndHashCode(callSuper = true)
public class Case extends BaseEntity {
    
    /**
     * 所属案件包ID
     */
    @Column(name = "case_package_id", nullable = false)
    private Long casePackageId;
    
    // 必填字段（来自PRD数据字典）
    
    /**
     * 借据编号（唯一标识）
     */
    @Column(name = "receipt_number", nullable = false, unique = true, length = 100)
    private String receiptNumber;
    
    /**
     * 身份证号（加密存储）
     */
    @Column(name = "debtor_id_card", nullable = false, length = 255)
    private String debtorIdCard;
    
    /**
     * 客户姓名（加密存储）
     */
    @Column(name = "debtor_name", nullable = false, length = 255)
    private String debtorName;
    
    /**
     * 手机号（加密存储）
     */
    @Column(name = "debtor_phone", nullable = false, length = 255)
    private String debtorPhone;
    
    /**
     * 借款项目/产品线
     */
    @Column(name = "loan_product", nullable = false, length = 100)
    private String loanProduct;
    
    /**
     * 贷款金额
     */
    @Column(name = "loan_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal loanAmount;
    
    /**
     * 剩余应还金额
     */
    @Column(name = "remaining_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal remainingAmount;
    
    /**
     * 逾期天数
     */
    @Column(name = "overdue_days", nullable = false)
    private Integer overdueDays;
    
    /**
     * 委托方
     */
    @Column(name = "consigner", nullable = false, length = 100)
    private String consigner;
    
    /**
     * 委托开始时间
     */
    @Column(name = "consign_start_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate consignStartDate;
    
    /**
     * 委托到期时间
     */
    @Column(name = "consign_end_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate consignEndDate;
    
    /**
     * 资方名称
     */
    @Column(name = "fund_provider", nullable = false, length = 100)
    private String fundProvider;
    
    // 可选字段（使用JSON存储以支持灵活扩展）
    
    /**
     * 债务信息（合同金额、期数、利率等）
     */
    @Column(name = "debt_info", columnDefinition = "JSON")
    private String debtInfo;
    
    /**
     * 债务人信息（性别、学历、地址等）
     */
    @Column(name = "debtor_info", columnDefinition = "JSON")
    private String debtorInfo;
    
    /**
     * 联系人信息
     */
    @Column(name = "contact_info", columnDefinition = "JSON")
    private String contactInfo;
    
    /**
     * 自定义字段
     */
    @Column(name = "custom_fields", columnDefinition = "JSON")
    private String customFields;
    
    // 案件状态与处置信息
    
    /**
     * 当前状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "current_status", nullable = false)
    private CaseStatus currentStatus = CaseStatus.PENDING_ASSIGNMENT;
    
    /**
     * 分配的处置机构ID
     */
    @Column(name = "assigned_org_id")
    private Long assignedOrgId;
    
    /**
     * 分配时间
     */
    @Column(name = "assigned_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime assignedAt;
    
    /**
     * 最新处置进展
     */
    @Column(name = "latest_progress", columnDefinition = "TEXT")
    private String latestProgress;
    
    /**
     * 已回款金额
     */
    @Column(name = "total_recovered", precision = 15, scale = 2)
    private BigDecimal totalRecovered = BigDecimal.ZERO;
    
    /**
     * 回款率（%）
     */
    @Column(name = "recovery_rate", precision = 5, scale = 2)
    private BigDecimal recoveryRate = BigDecimal.ZERO;
    
    /**
     * 案件凭证文件列表（JSON格式）
     */
    @Column(name = "attachments", columnDefinition = "JSON")
    @Convert(converter = StringListConverter.class)
    private List<String> attachments;
    
    // 关联关系
    
    /**
     * 所属案件包
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_package_id", insertable = false, updatable = false)
    private CasePackage casePackage;
    
    // 业务方法
    
    /**
     * 是否已分案
     */
    public boolean isAssigned() {
        return assignedOrgId != null && !CaseStatus.PENDING_ASSIGNMENT.equals(currentStatus);
    }
    
    /**
     * 是否在处置中
     */
    public boolean isProcessing() {
        return CaseStatus.PROCESSING.equals(currentStatus) || 
               CaseStatus.MEDIATING.equals(currentStatus) || 
               CaseStatus.LITIGATING.equals(currentStatus);
    }
    
    /**
     * 是否已结案
     */
    public boolean isClosed() {
        return CaseStatus.SETTLED.equals(currentStatus) || 
               CaseStatus.CLOSED.equals(currentStatus) || 
               CaseStatus.WITHDRAWN.equals(currentStatus);
    }
    
    /**
     * 获取逾期等级
     */
    public String getOverdueLevel() {
        if (overdueDays <= 30) {
            return "轻度逾期";
        } else if (overdueDays <= 90) {
            return "中度逾期";
        } else if (overdueDays <= 180) {
            return "重度逾期";
        } else {
            return "严重逾期";
        }
    }
    
    /**
     * 计算回款率
     */
    public void calculateRecoveryRate() {
        if (remainingAmount.compareTo(BigDecimal.ZERO) > 0) {
            this.recoveryRate = totalRecovered.divide(remainingAmount, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
    }
    
    /**
     * 获取案件风险等级
     */
    public String getRiskLevel() {
        // 基于逾期天数和金额计算风险等级
        int riskScore = 0;
        
        // 逾期天数权重
        if (overdueDays > 180) {
            riskScore += 40;
        } else if (overdueDays > 90) {
            riskScore += 30;
        } else if (overdueDays > 30) {
            riskScore += 20;
        } else {
            riskScore += 10;
        }
        
        // 金额权重
        if (remainingAmount.compareTo(new BigDecimal("100000")) > 0) {
            riskScore += 30;
        } else if (remainingAmount.compareTo(new BigDecimal("50000")) > 0) {
            riskScore += 20;
        } else {
            riskScore += 10;
        }
        
        if (riskScore >= 60) {
            return "高风险";
        } else if (riskScore >= 40) {
            return "中风险";
        } else {
            return "低风险";
        }
    }
}