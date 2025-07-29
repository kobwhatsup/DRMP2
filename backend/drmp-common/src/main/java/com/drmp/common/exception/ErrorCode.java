package com.drmp.common.exception;

/**
 * 错误码枚举
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
public enum ErrorCode {
    
    // 通用错误码 (10000-10999)
    SUCCESS(10000, "操作成功"),
    SYSTEM_ERROR(10001, "系统错误"),
    INVALID_PARAMETER(10002, "参数错误"),
    UNAUTHORIZED(10003, "未授权"),
    FORBIDDEN(10004, "禁止访问"),
    NOT_FOUND(10005, "资源不存在"),
    METHOD_NOT_ALLOWED(10006, "方法不允许"),
    REQUEST_TIMEOUT(10007, "请求超时"),
    TOO_MANY_REQUESTS(10008, "请求过于频繁"),
    INTERNAL_SERVER_ERROR(10009, "服务器内部错误"),
    
    // 用户相关错误码 (11000-11999)
    USER_NOT_FOUND(11001, "用户不存在"),
    USER_ALREADY_EXISTS(11002, "用户已存在"),
    USER_DISABLED(11003, "用户已禁用"),
    INVALID_USERNAME_OR_PASSWORD(11004, "用户名或密码错误"),
    PASSWORD_EXPIRED(11005, "密码已过期"),
    ACCOUNT_LOCKED(11006, "账户已锁定"),
    LOGIN_EXPIRED(11007, "登录已过期"),
    INVALID_TOKEN(11008, "无效的令牌"),
    
    // 机构相关错误码 (12000-12999)
    ORGANIZATION_NOT_FOUND(12001, "机构不存在"),
    ORGANIZATION_ALREADY_EXISTS(12002, "机构已存在"),
    ORGANIZATION_NOT_APPROVED(12003, "机构未审核通过"),
    ORGANIZATION_SUSPENDED(12004, "机构已暂停"),
    INVALID_ORGANIZATION_TYPE(12005, "无效的机构类型"),
    
    // 案件包相关错误码 (13000-13999)
    CASE_PACKAGE_NOT_FOUND(13001, "案件包不存在"),
    CASE_PACKAGE_NAME_EXISTS(13002, "案件包名称已存在"),
    CASE_PACKAGE_CANNOT_MODIFY(13003, "案件包状态不允许修改"),
    CASE_PACKAGE_CANNOT_DELETE(13004, "案件包状态不允许删除"),
    CASE_PACKAGE_CANNOT_PUBLISH(13005, "案件包状态不允许发布"),
    CASE_PACKAGE_CANNOT_WITHDRAW(13006, "案件包状态不允许撤回"),
    CASE_PACKAGE_NO_CASES(13007, "案件包中没有案件数据"),
    CASE_PACKAGE_ALREADY_ASSIGNED(13008, "案件包已分配，无法修改"),
    
    // 案件相关错误码 (14000-14999)
    CASE_NOT_FOUND(14001, "案件不存在"),
    CASE_RECEIPT_NUMBER_EXISTS(14002, "借据编号已存在"),
    CASE_CANNOT_DELETE(14003, "案件状态不允许删除"),
    CASE_CANNOT_ASSIGN(14004, "案件状态不允许分配"),
    CASE_ALREADY_ASSIGNED(14005, "案件已分配"),
    CASE_ALREADY_CLOSED(14006, "案件已结案"),
    INVALID_STATUS_TRANSITION(14007, "无效的状态转换"),
    CASE_ASSIGNMENT_FAILED(14008, "案件分配失败"),
    
    // 导入相关错误码 (15000-15999)
    IMPORT_FILE_EMPTY(15001, "导入文件为空"),
    IMPORT_FILE_FORMAT_ERROR(15002, "导入文件格式错误"),
    IMPORT_FILE_TOO_LARGE(15003, "导入文件过大"),
    IMPORT_DATA_VALIDATION_ERROR(15004, "导入数据验证失败"),
    IMPORT_TASK_NOT_FOUND(15005, "导入任务不存在"),
    IMPORT_TASK_TIMEOUT(15006, "导入任务超时"),
    
    // 分案相关错误码 (16000-16999)
    ASSIGNMENT_NOT_FOUND(16001, "分案记录不存在"),
    ASSIGNMENT_ALREADY_ACCEPTED(16002, "分案已接受"),
    ASSIGNMENT_ALREADY_REJECTED(16003, "分案已拒绝"),
    ASSIGNMENT_EXPIRED(16004, "分案已过期"),
    NO_AVAILABLE_DISPOSAL_ORG(16005, "没有可用的处置机构"),
    ASSIGNMENT_STRATEGY_ERROR(16006, "分案策略配置错误"),
    
    // 文件相关错误码 (17000-17999)
    FILE_NOT_FOUND(17001, "文件不存在"),
    FILE_UPLOAD_FAILED(17002, "文件上传失败"),
    FILE_DOWNLOAD_FAILED(17003, "文件下载失败"),
    FILE_DELETE_FAILED(17004, "文件删除失败"),
    UNSUPPORTED_FILE_TYPE(17005, "不支持的文件类型"),
    FILE_SIZE_EXCEEDED(17006, "文件大小超过限制"),
    
    // 权限相关错误码 (18000-18999)
    PERMISSION_DENIED(18001, "权限不足"),
    ROLE_NOT_FOUND(18002, "角色不存在"),
    PERMISSION_NOT_FOUND(18003, "权限不存在"),
    ROLE_ALREADY_ASSIGNED(18004, "角色已分配"),
    CANNOT_DELETE_SYSTEM_ROLE(18005, "无法删除系统角色"),
    
    // 业务逻辑错误码 (19000-19999)
    BUSINESS_LOGIC_ERROR(19001, "业务逻辑错误"),
    DATA_INTEGRITY_ERROR(19002, "数据完整性错误"),
    CONCURRENT_MODIFICATION_ERROR(19003, "并发修改错误"),
    RESOURCE_CONFLICT(19004, "资源冲突"),
    OPERATION_NOT_ALLOWED(19005, "操作不允许");
    
    private final int code;
    private final String message;
    
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}