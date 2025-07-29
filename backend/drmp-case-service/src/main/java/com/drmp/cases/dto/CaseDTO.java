package com.drmp.cases.dto;

import com.drmp.common.enums.CaseStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 案件数据传输对象
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Data
@Schema(description = "案件信息")
public class CaseDTO {
    
    @Schema(description = "案件ID")
    private Long id;
    
    @Schema(description = "所属案件包ID", required = true)
    @NotNull(message = "案件包ID不能为空")
    private Long casePackageId;
    
    @Schema(description = "案件包名称")
    private String casePackageName;
    
    // 必填字段
    
    @Schema(description = "借据编号", required = true)
    @NotBlank(message = "借据编号不能为空")
    @Size(max = 100, message = "借据编号长度不能超过100字符")
    private String receiptNumber;
    
    @Schema(description = "身份证号", required = true)
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$", 
             message = "身份证号格式不正确")
    private String debtorIdCard;
    
    @Schema(description = "客户姓名", required = true)
    @NotBlank(message = "客户姓名不能为空")
    @Size(max = 100, message = "客户姓名长度不能超过100字符")
    private String debtorName;
    
    @Schema(description = "手机号", required = true)
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String debtorPhone;
    
    @Schema(description = "借款项目/产品线", required = true)
    @NotBlank(message = "借款项目不能为空")
    @Size(max = 100, message = "借款项目长度不能超过100字符")
    private String loanProduct;
    
    @Schema(description = "贷款金额", required = true)
    @NotNull(message = "贷款金额不能为空")
    @DecimalMin(value = "0", message = "贷款金额不能为负数")
    private BigDecimal loanAmount;
    
    @Schema(description = "剩余应还金额", required = true)
    @NotNull(message = "剩余应还金额不能为空")
    @DecimalMin(value = "0", message = "剩余应还金额不能为负数")
    private BigDecimal remainingAmount;
    
    @Schema(description = "逾期天数", required = true)
    @NotNull(message = "逾期天数不能为空")
    @Min(value = 0, message = "逾期天数不能为负数")
    private Integer overdueDays;
    
    @Schema(description = "逾期等级")
    private String overdueLevel;
    
    @Schema(description = "委托方", required = true)
    @NotBlank(message = "委托方不能为空")
    @Size(max = 100, message = "委托方长度不能超过100字符")
    private String consigner;
    
    @Schema(description = "委托开始时间", required = true)
    @NotNull(message = "委托开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate consignStartDate;
    
    @Schema(description = "委托到期时间", required = true)
    @NotNull(message = "委托到期时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate consignEndDate;
    
    @Schema(description = "资方名称", required = true)
    @NotBlank(message = "资方名称不能为空")
    @Size(max = 100, message = "资方名称长度不能超过100字符")
    private String fundProvider;
    
    // 可选字段
    
    @Schema(description = "债务信息（JSON格式）")
    private String debtInfo;
    
    @Schema(description = "债务人信息（JSON格式）")
    private String debtorInfo;
    
    @Schema(description = "联系人信息（JSON格式）")
    private String contactInfo;
    
    @Schema(description = "自定义字段（JSON格式）")
    private String customFields;
    
    // 案件状态与处置信息
    
    @Schema(description = "当前状态")
    private CaseStatus currentStatus;
    
    @Schema(description = "分配的处置机构ID")
    private Long assignedOrgId;
    
    @Schema(description = "分配的处置机构名称")
    private String assignedOrgName;
    
    @Schema(description = "分配时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime assignedAt;
    
    @Schema(description = "最新处置进展")
    @Size(max = 2000, message = "处置进展长度不能超过2000字符")
    private String latestProgress;
    
    @Schema(description = "已回款金额")
    private BigDecimal totalRecovered;
    
    @Schema(description = "回款率（%）")
    private BigDecimal recoveryRate;
    
    @Schema(description = "案件凭证文件列表")
    private List<String> attachments;
    
    @Schema(description = "风险等级")
    private String riskLevel;
    
    @Schema(description = "是否已分案")
    private Boolean assigned;
    
    @Schema(description = "是否在处置中")
    private Boolean processing;
    
    @Schema(description = "是否已结案")
    private Boolean closed;
    
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    // 验证分组
    public interface Create {}
    public interface Update {}
    public interface Import {}
}