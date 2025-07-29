package com.drmp.user.dto;

import com.drmp.common.enums.OrganizationType;
import com.drmp.user.entity.Organization;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 机构数据传输对象
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Data
@Schema(description = "机构信息")
public class OrganizationDTO {
    
    @Schema(description = "机构ID")
    private Long id;
    
    @Schema(description = "机构名称", required = true)
    @NotBlank(message = "机构名称不能为空")
    @Size(max = 255, message = "机构名称长度不能超过255字符")
    private String name;
    
    @Schema(description = "机构类型", required = true, allowableValues = {"SOURCE", "DISPOSAL"})
    @NotNull(message = "机构类型不能为空")
    private OrganizationType type;
    
    @Schema(description = "子类型")
    @Size(max = 50, message = "子类型长度不能超过50字符")
    private String subType;
    
    @Schema(description = "机构状态")
    private Organization.OrganizationStatus status;
    
    @Schema(description = "联系人", required = true)
    @NotBlank(message = "联系人不能为空")
    @Size(max = 100, message = "联系人长度不能超过100字符")
    private String contactPerson;
    
    @Schema(description = "联系电话", required = true)
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "联系电话格式不正确")
    private String contactPhone;
    
    @Schema(description = "联系邮箱", required = true)
    @NotBlank(message = "联系邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100字符")
    private String contactEmail;
    
    @Schema(description = "办公地址")
    @Size(max = 1000, message = "办公地址长度不能超过1000字符")
    private String address;
    
    @Schema(description = "营业执照文件路径")
    private String businessLicense;
    
    @Schema(description = "法定代表人")
    @Size(max = 100, message = "法定代表人长度不能超过100字符")
    private String legalPerson;
    
    @Schema(description = "统一社会信用代码")
    @Pattern(regexp = "^[0-9A-HJ-NPQRTUWXY]{2}\\d{6}[0-9A-HJ-NPQRTUWXY]{10}$", 
             message = "统一社会信用代码格式不正确")
    private String unifiedCreditCode;
    
    @Schema(description = "注册资本（万元）")
    @DecimalMin(value = "0", message = "注册资本不能为负数")
    private BigDecimal registrationCapital;
    
    @Schema(description = "成立日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate establishDate;
    
    // 处置机构特有字段
    
    @Schema(description = "团队规模（人数）")
    @Min(value = 1, message = "团队规模至少为1人")
    private Integer teamSize;
    
    @Schema(description = "月处理案件能力")
    @Min(value = 1, message = "月处理能力至少为1件")
    private Integer monthlyCapacity;
    
    @Schema(description = "当前负载")
    private String currentLoad;
    
    @Schema(description = "服务区域列表")
    private List<String> serviceRegions;
    
    @Schema(description = "业务范围")
    private List<String> businessScope;
    
    @Schema(description = "处置类型")
    private List<String> disposalTypes;
    
    @Schema(description = "结算方式")
    private List<String> settlementMethods;
    
    @Schema(description = "合作案例描述")
    @Size(max = 2000, message = "合作案例描述长度不能超过2000字符")
    private String cooperationCases;
    
    @Schema(description = "机构描述")
    @Size(max = 2000, message = "机构描述长度不能超过2000字符")
    private String description;
    
    // 审核相关字段
    
    @Schema(description = "审核状态")
    private Organization.AuditStatus auditStatus;
    
    @Schema(description = "审核意见")
    private String auditComment;
    
    @Schema(description = "审核时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditTime;
    
    @Schema(description = "审核人ID")
    private Long auditBy;
    
    // 合同相关字段
    
    @Schema(description = "合同开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate contractStartDate;
    
    @Schema(description = "合同结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate contractEndDate;
    
    @Schema(description = "合同文件路径")
    private String contractFile;
    
    // 时间字段
    
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    // 验证分组
    public interface Create {}
    public interface Update {}
    public interface Audit {}
}