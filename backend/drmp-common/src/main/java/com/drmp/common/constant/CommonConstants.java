package com.drmp.common.constant;

/**
 * 通用常量类
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
public interface CommonConstants {
    
    /**
     * 成功标记
     */
    Integer SUCCESS = 200;
    
    /**
     * 失败标记
     */
    Integer FAIL = 500;
    
    /**
     * 参数校验失败
     */
    Integer VALIDATE_ERROR = 400;
    
    /**
     * 未授权
     */
    Integer UNAUTHORIZED = 401;
    
    /**
     * 禁止访问
     */
    Integer FORBIDDEN = 403;
    
    /**
     * 资源不存在
     */
    Integer NOT_FOUND = 404;
    
    /**
     * 编码
     */
    String UTF8 = "UTF-8";
    
    /**
     * JSON 资源
     */
    String CONTENT_TYPE = "application/json; charset=utf-8";
    
    /**
     * 默认分页大小
     */
    Integer DEFAULT_PAGE_SIZE = 10;
    
    /**
     * 最大分页大小
     */
    Integer MAX_PAGE_SIZE = 1000;
    
    /**
     * 逻辑删除 - 未删除
     */
    Integer NOT_DELETED = 0;
    
    /**
     * 逻辑删除 - 已删除
     */
    Integer DELETED = 1;
    
    /**
     * 启用状态
     */
    Integer STATUS_ENABLED = 1;
    
    /**
     * 禁用状态
     */
    Integer STATUS_DISABLED = 0;
    
    /**
     * 默认密码
     */
    String DEFAULT_PASSWORD = "123456";
    
    /**
     * 超级管理员角色
     */
    String ROLE_SUPER_ADMIN = "SUPER_ADMIN";
    
    /**
     * 系统管理员角色
     */
    String ROLE_ADMIN = "ADMIN";
    
    /**
     * 机构管理员角色
     */
    String ROLE_ORG_ADMIN = "ORG_ADMIN";
    
    /**
     * 普通用户角色
     */
    String ROLE_USER = "USER";
}