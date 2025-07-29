package com.drmp.cases.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 批量导入结果DTO
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Data
@Schema(description = "批量导入结果")
public class BatchImportResult {
    
    @Schema(description = "导入任务ID")
    private String taskId;
    
    @Schema(description = "文件名")
    private String fileName;
    
    @Schema(description = "总记录数")
    private Integer totalRecords;
    
    @Schema(description = "成功导入数")
    private Integer successCount;
    
    @Schema(description = "失败数")
    private Integer failureCount;
    
    @Schema(description = "跳过数")
    private Integer skipCount;
    
    @Schema(description = "导入状态")
    private ImportStatus status;
    
    @Schema(description = "导入进度（%）")
    private Integer progress;
    
    @Schema(description = "开始时间")
    private LocalDateTime startTime;
    
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
    
    @Schema(description = "耗时（秒）")
    private Long duration;
    
    @Schema(description = "错误信息")
    private String errorMessage;
    
    @Schema(description = "失败记录详情")
    private List<ImportError> errors;
    
    @Schema(description = "导入摘要")
    private ImportSummary summary;
    
    /**
     * 导入状态枚举
     */
    public enum ImportStatus {
        PENDING("待导入"),
        PROCESSING("导入中"),
        SUCCESS("导入成功"),
        PARTIAL_SUCCESS("部分成功"),
        FAILED("导入失败");
        
        private final String description;
        
        ImportStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 导入错误详情
     */
    @Data
    @Schema(description = "导入错误详情")
    public static class ImportError {
        
        @Schema(description = "行号")
        private Integer rowNumber;
        
        @Schema(description = "借据编号")
        private String receiptNumber;
        
        @Schema(description = "错误类型")
        private String errorType;
        
        @Schema(description = "错误信息")
        private String errorMessage;
        
        @Schema(description = "错误字段")
        private String errorField;
    }
    
    /**
     * 导入摘要
     */
    @Data
    @Schema(description = "导入摘要")
    public static class ImportSummary {
        
        @Schema(description = "平均贷款金额")
        private java.math.BigDecimal avgLoanAmount;
        
        @Schema(description = "总贷款金额")
        private java.math.BigDecimal totalLoanAmount;
        
        @Schema(description = "平均逾期天数")
        private Double avgOverdueDays;
        
        @Schema(description = "最大逾期天数")
        private Integer maxOverdueDays;
        
        @Schema(description = "最小逾期天数")
        private Integer minOverdueDays;
        
        @Schema(description = "按逾期等级分布")
        private java.util.Map<String, Integer> overdueDistribution;
        
        @Schema(description = "按资方分布")
        private java.util.Map<String, Integer> fundProviderDistribution;
        
        @Schema(description = "按产品线分布")
        private java.util.Map<String, Integer> productDistribution;
    }
    
    // 业务方法
    
    /**
     * 计算成功率
     */
    public Double getSuccessRate() {
        if (totalRecords == null || totalRecords == 0) {
            return 0.0;
        }
        return (successCount * 100.0) / totalRecords;
    }
    
    /**
     * 计算失败率
     */
    public Double getFailureRate() {
        if (totalRecords == null || totalRecords == 0) {
            return 0.0;
        }
        return (failureCount * 100.0) / totalRecords;
    }
    
    /**
     * 是否导入完成（成功或失败）
     */
    public boolean isCompleted() {
        return ImportStatus.SUCCESS.equals(status) || 
               ImportStatus.PARTIAL_SUCCESS.equals(status) || 
               ImportStatus.FAILED.equals(status);
    }
    
    /**
     * 是否有错误
     */
    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }
    
    /**
     * 创建成功结果
     */
    public static BatchImportResult success(String taskId, int totalRecords, int successCount) {
        BatchImportResult result = new BatchImportResult();
        result.setTaskId(taskId);
        result.setTotalRecords(totalRecords);
        result.setSuccessCount(successCount);
        result.setFailureCount(0);
        result.setSkipCount(0);
        result.setStatus(ImportStatus.SUCCESS);
        result.setProgress(100);
        result.setEndTime(LocalDateTime.now());
        return result;
    }
    
    /**
     * 创建部分成功结果
     */
    public static BatchImportResult partialSuccess(String taskId, int totalRecords, 
                                                 int successCount, int failureCount) {
        BatchImportResult result = new BatchImportResult();
        result.setTaskId(taskId);
        result.setTotalRecords(totalRecords);
        result.setSuccessCount(successCount);
        result.setFailureCount(failureCount);
        result.setSkipCount(0);
        result.setStatus(ImportStatus.PARTIAL_SUCCESS);
        result.setProgress(100);
        result.setEndTime(LocalDateTime.now());
        return result;
    }
    
    /**
     * 创建失败结果
     */
    public static BatchImportResult failure(String taskId, String errorMessage) {
        BatchImportResult result = new BatchImportResult();
        result.setTaskId(taskId);
        result.setStatus(ImportStatus.FAILED);
        result.setProgress(0);
        result.setErrorMessage(errorMessage);
        result.setEndTime(LocalDateTime.now());
        return result;
    }
}