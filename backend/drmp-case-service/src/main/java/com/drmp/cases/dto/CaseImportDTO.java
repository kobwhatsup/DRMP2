package com.drmp.cases.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * 案件导入数据传输对象
 * 用于Excel/CSV文件导入时的数据传输
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Data
@Schema(description = "案件导入信息")
public class CaseImportDTO {
    
    @Schema(description = "行号（用于错误定位）")
    private Integer rowNumber;
    
    // 必填字段
    
    @Schema(description = "借据编号", required = true)
    private String receiptNumber;
    
    @Schema(description = "身份证号", required = true)
    private String debtorIdCard;
    
    @Schema(description = "客户姓名", required = true)
    private String debtorName;
    
    @Schema(description = "手机号", required = true)
    private String debtorPhone;
    
    @Schema(description = "借款项目/产品线", required = true)
    private String loanProduct;
    
    @Schema(description = "贷款金额", required = true)
    private BigDecimal loanAmount;
    
    @Schema(description = "剩余应还金额", required = true)
    private BigDecimal remainingAmount;
    
    @Schema(description = "逾期天数", required = true)
    private Integer overdueDays;
    
    @Schema(description = "委托方", required = true)
    private String consigner;
    
    @Schema(description = "委托开始时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate consignStartDate;
    
    @Schema(description = "委托到期时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate consignEndDate;
    
    @Schema(description = "资方名称", required = true)
    private String fundProvider;
    
    // 建议填写字段（动态字段，使用Map存储）
    
    @Schema(description = "债务信息字段")
    private Map<String, Object> debtFields;
    
    @Schema(description = "债务人信息字段")
    private Map<String, Object> debtorFields;
    
    @Schema(description = "联系人信息字段")
    private Map<String, Object> contactFields;
    
    @Schema(description = "自定义字段")
    private Map<String, Object> customFields;
    
    // 验证结果
    
    @Schema(description = "验证是否通过")
    private Boolean valid;
    
    @Schema(description = "验证错误信息")
    private String errorMessage;
    
    // 业务方法
    
    /**
     * 添加验证错误
     */
    public void addError(String error) {
        this.valid = false;
        if (this.errorMessage == null) {
            this.errorMessage = error;
        } else {
            this.errorMessage += "; " + error;
        }
    }
    
    /**
     * 设置为验证通过
     */
    public void setValid() {
        this.valid = true;
        this.errorMessage = null;
    }
    
    /**
     * 获取债务信息字段值
     */
    public Object getDebtField(String key) {
        return debtFields != null ? debtFields.get(key) : null;
    }
    
    /**
     * 设置债务信息字段值
     */
    public void setDebtField(String key, Object value) {
        if (debtFields == null) {
            debtFields = new java.util.HashMap<>();
        }
        debtFields.put(key, value);
    }
    
    /**
     * 获取债务人信息字段值
     */
    public Object getDebtorField(String key) {
        return debtorFields != null ? debtorFields.get(key) : null;
    }
    
    /**
     * 设置债务人信息字段值
     */
    public void setDebtorField(String key, Object value) {
        if (debtorFields == null) {
            debtorFields = new java.util.HashMap<>();
        }
        debtorFields.put(key, value);
    }
    
    /**
     * 获取联系人信息字段值
     */
    public Object getContactField(String key) {
        return contactFields != null ? contactFields.get(key) : null;
    }
    
    /**
     * 设置联系人信息字段值
     */
    public void setContactField(String key, Object value) {
        if (contactFields == null) {
            contactFields = new java.util.HashMap<>();
        }
        contactFields.put(key, value);
    }
    
    /**
     * 获取自定义字段值
     */
    public Object getCustomField(String key) {
        return customFields != null ? customFields.get(key) : null;
    }
    
    /**
     * 设置自定义字段值
     */
    public void setCustomField(String key, Object value) {
        if (customFields == null) {
            customFields = new java.util.HashMap<>();
        }
        customFields.put(key, value);
    }
}