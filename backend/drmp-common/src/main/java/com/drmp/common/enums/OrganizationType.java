package com.drmp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 机构类型枚举
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum OrganizationType {
    
    /**
     * 案源机构（委托方）
     */
    SOURCE("SOURCE", "案源机构"),
    
    /**
     * 处置机构（服务方）
     */
    DISPOSAL("DISPOSAL", "处置机构");
    
    private final String code;
    private final String description;
    
    /**
     * 根据代码获取枚举
     */
    public static OrganizationType fromCode(String code) {
        for (OrganizationType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的机构类型: " + code);
    }
}