package com.drmp.cases.dto;

import com.drmp.cases.entity.CasePackage;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 案件包数据传输对象
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Data
@Schema(description = "案件包信息")
public class CasePackageDTO {
    
    @Schema(description = "案件包ID")
    private Long id;
    
    @Schema(description = "案件包名称", required = true)
    @NotBlank(message = "案件包名称不能为空")
    @Size(max = 255, message = "案件包名称长度不能超过255字符")
    private String name;
    
    @Schema(description = "案件包描述")
    @Size(max = 2000, message = "案件包描述长度不能超过2000字符")
    private String description;
    
    @Schema(description = "案源机构ID", required = true)
    @NotNull(message = "案源机构不能为空")
    private Long sourceOrgId;
    
    @Schema(description = "案源机构名称")
    private String sourceOrgName;
    
    @Schema(description = "案件总数量")
    private Integer totalCount;
    
    @Schema(description = "案件总金额")
    private BigDecimal totalAmount;
    
    @Schema(description = "已分案数量")
    private Integer assignedCount;
    
    @Schema(description = "已分案金额")
    private BigDecimal assignedAmount;
    
    @Schema(description = "剩余未分案数量")
    private Integer remainingCount;
    
    @Schema(description = "剩余未分案金额")
    private BigDecimal remainingAmount;
    
    @Schema(description = "分案进度（%）")
    private Integer assignmentProgress;
    
    @Schema(description = "案件包状态")
    private CasePackage.CasePackageStatus status;
    
    @Schema(description = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;
    
    @Schema(description = "期望回款率（%）")
    @DecimalMin(value = "0", message = "期望回款率不能为负数")
    @DecimalMax(value = "100", message = "期望回款率不能超过100%")
    private BigDecimal expectedRecoveryRate;
    
    @Schema(description = "期望处置周期（天）")
    @Min(value = 1, message = "期望处置周期至少为1天")
    @Max(value = 365, message = "期望处置周期不能超过365天")
    private Integer expectedPeriod;
    
    @Schema(description = "偏好处置方式")
    private List<String> preferredMethods;
    
    @Schema(description = "分案策略配置")
    private String assignmentStrategy;
    
    @Schema(description = "导入文件路径")
    private String importFilePath;
    
    @Schema(description = "导入状态")
    private CasePackage.ImportStatus importStatus;
    
    @Schema(description = "导入进度（%）")
    private Integer importProgress;
    
    @Schema(description = "导入错误信息")
    private String importErrorMsg;
    
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    // 验证分组
    public interface Create {}
    public interface Update {}
    public interface Publish {}
}