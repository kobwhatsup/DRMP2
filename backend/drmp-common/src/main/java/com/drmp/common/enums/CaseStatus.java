package com.drmp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 案件状态枚举
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum CaseStatus {
    
    /**
     * 待分案
     */
    PENDING_ASSIGNMENT("PENDING_ASSIGNMENT", "待分案"),
    
    /**
     * 已分案
     */
    ASSIGNED("ASSIGNED", "已分案"),
    
    /**
     * 处置中
     */
    PROCESSING("PROCESSING", "处置中"),
    
    /**
     * 调解中
     */
    MEDIATING("MEDIATING", "调解中"),
    
    /**
     * 诉讼中
     */
    LITIGATING("LITIGATING", "诉讼中"),
    
    /**
     * 已和解
     */
    SETTLED("SETTLED", "已和解"),
    
    /**
     * 已结案
     */
    CLOSED("CLOSED", "已结案"),
    
    /**
     * 已撤回
     */
    WITHDRAWN("WITHDRAWN", "已撤回"),
    
    /**
     * 已暂停
     */
    SUSPENDED("SUSPENDED", "已暂停");
    
    private final String code;
    private final String description;
    
    /**
     * 根据代码获取枚举
     */
    public static CaseStatus fromCode(String code) {
        for (CaseStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的案件状态: " + code);
    }
    
    /**
     * 判断是否为终态
     */
    public boolean isFinalStatus() {
        return this == SETTLED || this == CLOSED || this == WITHDRAWN;
    }
}