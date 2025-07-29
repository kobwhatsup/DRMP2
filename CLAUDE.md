# DRMP开发指南 - Claude AI Assistant Context

## 项目概述

全国分散诉调平台（DRMP）是一个B2B的个贷不良资产处置平台，连接案源机构（银行、消金公司等）与处置机构（律所、调解中心等）。平台的核心价值在于通过智能匹配和流程优化，解决信息不对称问题，提升不良资产处置效率。

### 关键业务概念
- **案源机构（委托方）**：提供不良资产案件的委托方，包括银行、消费金融公司、网络贷款公司、助贷公司、小额贷款公司、资产管理公司（AMC）等
- **处置机构（服务方）**：承接并处理不良资产案件的服务方，包括调解中心、律师事务所及其他专业处置机构
- **案件包**：案源机构批量发布的一组不良资产案件，包含债务人信息、债务信息、案件凭证等
- **智能分案**：根据地域、金额、账龄、业绩等因素自动匹配处置机构，支持多维度策略组合
- **智调系统（IDS）**：一线作业人员使用的处置工具，提供调解、诉讼、催收等具体功能，通过API与DRMP集成
- **结算方式**：
  - **全风险**：处置机构按回款一定比例分佣
  - **半风险**：处置机构按固定费用加回款分佣
  - **无风险**：处置机构按每个案件固定金额支付费用

## 技术架构决策

### 前端架构
```typescript
// 现代化前端技术栈
- React 18 + TypeScript：支持并发特性的现代化框架，提供类型安全的开发体验
- Ant Design 5.x：企业级UI组件库，支持设计令牌和主题定制
- TanStack Query (React Query)：强大的数据获取和缓存管理，支持乐观更新、无限滚动等
- React Hook Form：高性能表单处理，减少不必要的重渲染
- Zustand：轻量级状态管理，用于全局状态和用户会话
- React Router v6：声明式路由管理
- Vite：快速构建工具，支持热模块替换(HMR)
- MSW (Mock Service Worker)：API模拟和测试
```

### 后端架构
```java
// 高性能微服务技术栈
- Java 17 (LTS)：企业级开发语言，支持最新语言特性和性能优化
- Spring Boot 3.x：支持原生镜像和新一代性能优化
- Spring Cloud Alibaba：中国本土化微服务解决方案
  - Nacos：服务注册发现与配置中心
  - Sentinel：流量控制、熔断降级、系统自适应保护
  - Seata：分布式事务解决方案
- Spring Security 6 + OAuth2/JWT：统一认证授权架构
- Spring Data JPA + Hibernate 6：支持多数据源和读写分离
- MySQL 8.0 + ShardingSphere：分库分表解决方案，支持亿级数据
- Redis Cluster 7.x：分布式缓存和会话管理
- RocketMQ 5.x：高性能、低延迟的消息中间件
- Elasticsearch 8.x：全文搜索和复杂查询引擎
```

### 微服务架构规划

采用领域驱动设计(DDD)进行微服务划分，确保高内聚低耦合：

```yaml
# 核心业务微服务
services:
  # 用户域服务
  user-service:
    description: 用户与机构管理
    responsibilities:
      - 机构注册认证（案源机构、处置机构）
      - 用户身份认证与授权（OAuth2/JWT）
      - RBAC权限管理（角色、权限、资源）
      - 多因子认证(MFA)和设备管理
    database: user_db (分库)
    cache: user_cache
    
  # 案件域服务  
  case-service:
    description: 案件管理核心服务
    responsibilities:
      - 案件包批量导入（Excel/CSV，异步处理）
      - 案件信息管理（债务人、债权、案件材料）
      - 案件状态流转（待分案→已分案→处置中→已结案）
      - 案件查询检索（支持复杂条件筛选）
    database: case_db (按机构分表，时间分区)
    search_engine: elasticsearch
    message_queue: case_events
    
  # 智能分案域服务
  assignment-service:
    description: 智能分案引擎
    responsibilities:
      - 多维度匹配算法（地域、金额、账龄、专业领域）
      - 处置机构能力评估（历史业绩，负载分析）
      - 机器学习推荐系统（协同过滤、内容推荐）
      - 分案策略配置管理
    algorithm_engine: TensorFlow/PyTorch
    real_time_compute: Apache Flink
    
  # 调解域服务
  mediation-service:
    description: 调解处置服务
    responsibilities:
      - 调解流程管理（调解方案生成、进度跟踪）
      - 电子协议生成与在线签署
      - 与IDS系统实时集成（API+消息队列）
      - 调解记录和凭证管理
    integration: IDS_API
    e_signature: 电子签章平台
    
  # 诉讼域服务
  litigation-service:
    description: 诉讼处置服务  
    responsibilities:
      - 法律文书批量生成（模板化、智能填充）
      - 机器人批量立案（对接法院系统）
      - 诉讼进度跟踪和案件查询
      - 法律风险评估和建议
    external_api: 法院电子诉讼平台
    document_template: 法律文书模板引擎
    
  # 结算域服务
  settlement-service:
    description: 对账结算服务
    responsibilities:
      - 回款数据统计分析
      - 自动对账和差异处理
      - 费用结算（全风险、半风险、无风险）
      - 财务报表生成
    calculation_engine: 多种结算模式支持
    financial_integration: 财务系统集成

# 基础设施微服务    
infrastructure_services:
  # 通知服务
  notification-service:
    description: 统一通知服务
    channels: [站内信, 短信, 邮件, 移动推送, 微信公众号]
    message_queue: notification_mq
    template_engine: 消息模板管理
    
  # 文件服务
  file-service:
    description: 分布式文件存储
    storage: MinIO集群/OSS
    features: [分片上传, 断点续传, CDN加速, 文件预览]
    capacity: PB级存储支持
    
  # 数据分析服务
  analytics-service:
    description: 数据分析与报表
    olap_engine: ClickHouse
    visualization: Apache Superset
    reports: [业绩看板, 效能分析, 趋势预测]
    
  # 网关服务
  gateway-service:
    description: API网关
    features: [路由转发, 限流熔断, 认证鉴权, 监控日志]
    implementation: Spring Cloud Gateway + Sentinel
```

## 核心功能实现指南

### 1. 智能分案引擎
```java
// 分案策略接口
public interface AssignmentStrategy {
    List<DisposalOrg> match(CasePackage casePackage, List<DisposalOrg> orgs);
}

// 策略实现示例
@Component
public class RegionBasedStrategy implements AssignmentStrategy {
    // 按地域匹配
}

@Component
public class PerformanceBasedStrategy implements AssignmentStrategy {
    // 按历史业绩匹配
}

// 策略模式 + 责任链模式组合使用
```

### 2. 案件批量导入
```typescript
// 前端处理大文件上传
const handleBatchImport = async (file: File) => {
  // 1. 前端预校验（文件格式、大小）
  // 2. 分片上传（处理超大文件）
  // 3. 后端异步处理
  // 4. WebSocket实时反馈进度
};
```

### 3. 权限控制（RBAC）
```java
// 基于注解的权限控制
@PreAuthorize("hasRole('CASE_MANAGER') and #orgId == authentication.principal.orgId")
public CasePackage publishCasePackage(Long orgId, CasePackageDTO dto) {
    // 业务逻辑
}
```

### 4. 文件存储方案
```yaml
# 考虑到每个案件可能有100MB+的材料，年处理1000万案件
storage:
  type: "object-storage"  # 使用对象存储（OSS/COS）
  structure:
    - /orgs/{orgId}/cases/{caseId}/documents/
    - /orgs/{orgId}/cases/{caseId}/evidence/
  features:
    - 分片上传
    - CDN加速
    - 定期归档
```

## 数据库设计要点

### 核心表结构
基于PRD中的数据字典设计：

```sql
-- 机构表
CREATE TABLE organizations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL COMMENT '机构名称',
    type ENUM('SOURCE', 'DISPOSAL') NOT NULL COMMENT '机构类型：案源/处置',
    sub_type VARCHAR(50) COMMENT '子类型：银行/消金/网贷/调解中心/律所等',
    status ENUM('PENDING', 'ACTIVE', 'SUSPENDED') DEFAULT 'PENDING',
    contact_person VARCHAR(100) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    address TEXT COMMENT '办公地址',
    business_license VARCHAR(255) COMMENT '营业执照文件路径',
    contract_start_date DATE COMMENT '合同起始日期',
    -- 处置机构特有字段
    team_size INT COMMENT '团队规模',
    monthly_capacity INT COMMENT '每月处理案件数量',
    current_load VARCHAR(50) COMMENT '当前负载',
    service_regions JSON COMMENT '服务区域（省份列表）',
    business_scope JSON COMMENT '业务范围',
    disposal_types JSON COMMENT '处置类型',
    settlement_methods JSON COMMENT '结算方式',
    cooperation_cases TEXT COMMENT '合作案例',
    description TEXT COMMENT '机构描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_type_status (type, status),
    INDEX idx_regions (service_regions(100))
);

-- 案件包表
CREATE TABLE case_packages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    source_org_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL COMMENT '案件包名称',
    case_count INT NOT NULL COMMENT '案件数量',
    total_amount DECIMAL(15,2) NOT NULL COMMENT '总欠款金额',
    expected_recovery_rate DECIMAL(5,2) COMMENT '期望回款率',
    expected_period INT COMMENT '期望处置周期（天）',
    preferred_methods JSON COMMENT '偏好处置方式',
    assignment_strategy JSON COMMENT '分案策略',
    status ENUM('PENDING', 'ASSIGNED', 'IN_PROGRESS', 'CLOSED') DEFAULT 'PENDING',
    published_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (source_org_id) REFERENCES organizations(id),
    INDEX idx_source_status (source_org_id, status),
    INDEX idx_published (published_at)
);

-- 案件表（按月分表）
CREATE TABLE cases_202407 (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    case_package_id BIGINT NOT NULL,
    -- 必填字段
    receipt_number VARCHAR(100) NOT NULL COMMENT '借据编号',
    debtor_id_card VARCHAR(64) NOT NULL COMMENT '身份证号（加密）',
    debtor_name VARCHAR(64) NOT NULL COMMENT '客户姓名（加密）',
    debtor_phone VARCHAR(64) NOT NULL COMMENT '手机号（加密）',
    loan_product VARCHAR(100) NOT NULL COMMENT '借款项目/产品线',
    loan_amount DECIMAL(15,2) NOT NULL COMMENT '贷款金额',
    remaining_amount DECIMAL(15,2) NOT NULL COMMENT '剩余应还金额',
    overdue_days INT NOT NULL COMMENT '逾期天数',
    consigner VARCHAR(100) NOT NULL COMMENT '委托方',
    consign_start_date DATE NOT NULL COMMENT '委托开始时间',
    consign_end_date DATE NOT NULL COMMENT '委托到期时间',
    fund_provider VARCHAR(100) NOT NULL COMMENT '资方名称',
    -- 可选字段（使用JSON存储以支持灵活扩展）
    debt_info JSON COMMENT '债务信息（合同金额、期数、利率等）',
    debtor_info JSON COMMENT '债务人信息（性别、学历、地址等）',
    contact_info JSON COMMENT '联系人信息',
    custom_fields JSON COMMENT '自定义字段',
    -- 案件状态与处置信息
    current_status ENUM('PENDING', 'IN_PROGRESS', 'SETTLED', 'CLOSED') DEFAULT 'PENDING',
    assigned_org_id BIGINT COMMENT '分配的处置机构ID',
    assigned_at TIMESTAMP NULL COMMENT '分配时间',
    latest_progress TEXT COMMENT '最新进展',
    total_recovered DECIMAL(15,2) DEFAULT 0 COMMENT '已回款金额',
    recovery_rate DECIMAL(5,2) DEFAULT 0 COMMENT '回款率',
    -- 文件附件
    attachments JSON COMMENT '案件凭证文件列表',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (case_package_id) REFERENCES case_packages(id),
    FOREIGN KEY (assigned_org_id) REFERENCES organizations(id),
    UNIQUE KEY uk_receipt (receipt_number),
    INDEX idx_package_status (case_package_id, current_status),
    INDEX idx_assigned_org (assigned_org_id),
    INDEX idx_overdue (overdue_days),
    INDEX idx_amount (remaining_amount)
);

-- 分案记录表
CREATE TABLE assignments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    case_package_id BIGINT NOT NULL,
    source_org_id BIGINT NOT NULL,
    disposal_org_id BIGINT NOT NULL,
    assignment_type ENUM('INTELLIGENT', 'MANUAL', 'AUTO_ACCEPT') NOT NULL,
    strategy_used VARCHAR(100) COMMENT '使用的分案策略',
    match_score DECIMAL(5,2) COMMENT '匹配分数',
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    accepted_at TIMESTAMP NULL COMMENT '接受时间',
    status ENUM('PENDING', 'ACCEPTED', 'REJECTED', 'EXPIRED') DEFAULT 'PENDING',
    reject_reason TEXT COMMENT '拒绝原因',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (case_package_id) REFERENCES case_packages(id),
    FOREIGN KEY (source_org_id) REFERENCES organizations(id),
    FOREIGN KEY (disposal_org_id) REFERENCES organizations(id),
    INDEX idx_package_status (case_package_id, status),
    INDEX idx_disposal_org (disposal_org_id, status),
    INDEX idx_assigned_time (assigned_at)
);

-- 回款记录表
CREATE TABLE repayments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    case_id BIGINT NOT NULL,
    amount DECIMAL(15,2) NOT NULL COMMENT '回款金额',
    repay_date DATE NOT NULL COMMENT '回款日期',
    evidence_files JSON COMMENT '回款凭证文件',
    submitted_by BIGINT NOT NULL COMMENT '提交机构ID',
    verified_by BIGINT NULL COMMENT '确认机构ID',
    verification_status ENUM('PENDING', 'VERIFIED', 'DISPUTED') DEFAULT 'PENDING',
    dispute_reason TEXT COMMENT '异议原因',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_case_date (case_id, repay_date),
    INDEX idx_verification (verification_status, created_at)
);
```

### 性能优化建议
1. **分库分表**：按机构ID或时间维度分表
2. **读写分离**：主从复制，读多写少场景
3. **缓存策略**：Redis缓存热点数据
4. **索引优化**：针对高频查询建立复合索引

## API设计规范

### RESTful API示例
```typescript
// API路径设计
POST   /api/v1/organizations/register     // 机构注册
POST   /api/v1/case-packages              // 发布案件包
GET    /api/v1/case-packages/{id}         // 获取案件包详情
PUT    /api/v1/cases/{id}/status          // 更新案件状态
POST   /api/v1/assignments/auto           // 触发智能分案

// 统一响应格式
interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
  timestamp: number;
}
```

### 错误处理
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException e) {
        return ApiResponse.error(e.getCode(), e.getMessage());
    }
}
```

## 安全架构与实现

### 数据安全实现

#### 1. 敏感数据加密策略
```java
// 多层次加密方案
@Component
public class DataEncryptionService {
    
    // PII数据使用国密SM4算法
    @Encrypt(algorithm = "SM4", level = "HIGH")
    private String encryptPII(String data) {
        // 身份证、手机号等敏感信息
        return sm4Cipher.encrypt(data, getEncryptionKey("PII"));
    }
    
    // 业务数据使用AES-256
    @Encrypt(algorithm = "AES256", level = "MEDIUM") 
    private String encryptBusiness(String data) {
        return aesCipher.encrypt(data, getEncryptionKey("BUSINESS"));
    }
    
    // 支持密文检索的同态加密
    @SearchableEncrypt
    private String encryptSearchable(String data) {
        return fheService.encrypt(data);
    }
}

// 数据库字段级加密
@Entity
public class Case {
    @Column
    @Encrypted(algorithm = "SM4")
    private String debtorIdCard;
    
    @Column  
    @Encrypted(algorithm = "AES256")
    private String debtorPhone;
    
    @Column
    @Masked(pattern = "***")  // 数据脱敏展示
    private String debtorName;
}
```

#### 2. 密钥管理系统
```java
// 基于HSM的密钥管理
@Service
public class KeyManagementService {
    
    // 密钥分级管理
    public enum KeyLevel {
        MASTER,    // 主密钥(HSM存储)
        DATA,      // 数据加密密钥  
        SESSION    // 会话密钥
    }
    
    // 密钥轮换机制
    @Scheduled(cron = "0 0 2 1 * ?") // 每月轮换
    public void rotateDataKeys() {
        keyRepository.rotateKeys(KeyLevel.DATA);
    }
}
```

### 身份认证与授权

#### 1. OAuth2 + JWT架构
```java
// 统一认证服务
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    // 多因子认证登录
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        // 1. 用户名密码验证
        User user = userService.authenticate(request.getUsername(), request.getPassword());
        
        // 2. MFA验证（短信/邮箱/TOTP）
        if (user.isMfaEnabled()) {
            mfaService.sendCode(user);
            return AuthResponse.requiresMfa(user.getId());
        }
        
        // 3. 设备指纹验证
        if (!deviceService.isTrustedDevice(request.getDeviceId(), user.getId())) {
            return AuthResponse.requiresDeviceAuth();
        }
        
        // 4. 生成JWT令牌
        TokenPair tokens = jwtService.generateTokens(user);
        
        // 5. 记录登录日志
        auditService.logLogin(user, request.getClientInfo());
        
        return AuthResponse.success(tokens);
    }
}

// JWT令牌服务
@Service
public class JwtTokenService {
    
    // 令牌配置
    private static final int ACCESS_TOKEN_EXPIRE = 2 * 60 * 60; // 2小时
    private static final int REFRESH_TOKEN_EXPIRE = 7 * 24 * 60 * 60; // 7天
    
    public TokenPair generateTokens(User user) {
        // Access Token（包含用户信息和权限）
        String accessToken = Jwts.builder()
            .setSubject(user.getId().toString())
            .claim("orgId", user.getOrgId())
            .claim("roles", user.getRoles())
            .claim("permissions", user.getPermissions())
            .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE * 1000))
            .signWith(getPrivateKey())
            .compact();
            
        // Refresh Token（仅用于刷新）
        String refreshToken = Jwts.builder()
            .setSubject(user.getId().toString())
            .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE * 1000))
            .signWith(getPrivateKey())
            .compact();
            
        return new TokenPair(accessToken, refreshToken);
    }
}
```

#### 2. 细粒度权限控制
```java
// 基于注解的权限控制
@PreAuthorize("hasPermission('CASE', 'READ') and #orgId == authentication.principal.orgId")
@GetMapping("/cases/{orgId}")
public List<Case> getCases(@PathVariable Long orgId) {
    return caseService.findByOrgId(orgId);
}

// 数据权限过滤器
@Component
public class DataPermissionInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        UserPrincipal user = SecurityUtils.getCurrentUser();
        
        // 机构数据隔离
        if (user.hasRole("ORG_USER")) {
            DataScope.setOrgId(user.getOrgId());
        }
        
        // 部门数据隔离  
        if (user.hasRole("DEPT_USER")) {
            DataScope.setDeptId(user.getDeptId());
        }
        
        return true;
    }
}
```

### 网络安全防护

#### 1. API安全网关
```java
// 基于Spring Cloud Gateway的安全网关
@Component  
public class SecurityGatewayFilter implements GatewayFilter {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // 1. API签名验证
        if (!apiSignatureService.verify(request)) {
            return responseUnauthorized(exchange);
        }
        
        // 2. 限流控制
        if (!rateLimitService.isAllowed(request)) {
            return responseRateLimited(exchange);
        }
        
        // 3. IP白名单验证
        if (!ipWhitelistService.isAllowed(request.getRemoteAddress())) {
            return responseForbidden(exchange);
        }
        
        // 4. 请求日志记录
        auditService.logApiRequest(request);
        
        return chain.filter(exchange);
    }
}

// API签名算法
@Service
public class ApiSignatureService {
    
    public boolean verify(ServerHttpRequest request) {
        String timestamp = request.getHeaders().getFirst("X-Timestamp");
        String nonce = request.getHeaders().getFirst("X-Nonce");
        String signature = request.getHeaders().getFirst("X-Signature");
        
        // 检查时间戳（防重放攻击）
        if (Math.abs(System.currentTimeMillis() - Long.parseLong(timestamp)) > 300000) {
            return false; // 5分钟有效期
        }
        
        // 检查随机数（防重放攻击）
        if (nonceCache.exists(nonce)) {
            return false;
        }
        nonceCache.set(nonce, "1", 300); // 缓存5分钟
        
        // 验证签名
        String expectedSignature = generateSignature(request, timestamp, nonce);
        return signature.equals(expectedSignature);
    }
    
    private String generateSignature(HttpServletRequest request, String timestamp, String nonce) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String body = getRequestBody(request);
        
        String data = method + uri + body + timestamp + nonce;
        return HmacUtils.hmacSha256Hex(getSecretKey(), data);
    }
}
```

#### 2. 安全审计与监控
```java
// 安全审计服务
@Service
public class SecurityAuditService {
    
    // 登录审计
    @EventListener
    public void handleLoginEvent(LoginEvent event) {
        SecurityAuditLog log = new SecurityAuditLog();
        log.setUserId(event.getUserId());
        log.setAction("LOGIN");
        log.setIpAddress(event.getIpAddress());
        log.setDeviceId(event.getDeviceId());
        log.setSuccess(event.isSuccess());
        log.setTimestamp(event.getTimestamp());
        log.setRiskLevel(calculateRiskLevel(event));
        
        auditLogRepository.save(log);
        
        // 异常登录告警
        if (log.getRiskLevel() > 3) {
            alertService.sendSecurityAlert(log);
        }
    }
    
    // 数据访问审计
    @Around("@annotation(DataAccess)")
    public Object auditDataAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        DataAccessLog log = new DataAccessLog();
        log.setUserId(SecurityUtils.getCurrentUserId());
        log.setResource(joinPoint.getSignature().getName());
        log.setStartTime(System.currentTimeMillis());
        
        try {
            Object result = joinPoint.proceed();
            log.setSuccess(true);
            return result;
        } catch (Exception e) {
            log.setSuccess(false);
            log.setErrorMessage(e.getMessage());
            throw e;
        } finally {
            log.setEndTime(System.currentTimeMillis());
            dataAccessLogRepository.save(log);
        }
    }
}
```

### 合规性实现

#### 1. 个人信息保护法合规
```java
// 个人信息处理记录
@Entity
public class PersonalDataProcessingRecord {
    private String dataSubject;        // 数据主体
    private String dataType;           // 数据类型
    private String processingPurpose;  // 处理目的
    private String legalBasis;         // 法律依据
    private Date processingTime;       // 处理时间
    private String processor;          // 处理者
    private boolean consentGiven;      // 是否获得同意
}

// 数据主体权利实现
@RestController
@RequestMapping("/privacy")
public class PrivacyController {
    
    // 查阅权
    @GetMapping("/data/{debtorId}")
    public PersonalDataReport getPersonalData(@PathVariable String debtorId) {
        return privacyService.generateDataReport(debtorId);
    }
    
    // 更正权
    @PutMapping("/data/{debtorId}")
    public void updatePersonalData(@PathVariable String debtorId, @RequestBody DataUpdateRequest request) {
        privacyService.updatePersonalData(debtorId, request);
    }
    
    // 删除权（被遗忘权）
    @DeleteMapping("/data/{debtorId}")
    public void deletePersonalData(@PathVariable String debtorId) {
        privacyService.deletePersonalData(debtorId);
    }
}
```

## 监控与日志

### 关键指标监控
```yaml
metrics:
  business:
    - 日活跃机构数
    - 案件处理量
    - 回款率
    - 平均处置周期
  technical:
    - API响应时间
    - 错误率
    - 数据库连接池
    - Redis命中率
```

### 日志规范
```java
// 使用MDC记录请求上下文
MDC.put("traceId", UUID.randomUUID().toString());
MDC.put("userId", SecurityUtils.getCurrentUserId());
log.info("案件包发布成功, packageId: {}, caseCount: {}", packageId, caseCount);
```

## 开发注意事项

### 代码组织架构
```
- Controller层：仅处理HTTP请求，参数验证，不包含业务逻辑
- Service层：核心业务逻辑，事务控制，状态管理
- Repository/DAO层：数据访问，避免复杂查询，使用分页查询
- Entity：数据库实体类，与JPA映射
- DTO：数据传输对象，用于各层间数据传递
- VO：视图对象，用于前端展示
- Converter/Mapper：实体类转换，使用MapStruct自动生成
```

### 核心业务实现要点
#### 1. 案件批量导入策略
```java
// 分阶段导入策略
@Service
public class CaseBatchImportService {
    
    // 第一阶段：文件解析和数据校验
    public BatchImportResult parseAndValidate(MultipartFile file) {
        // 1. 文件格式检查
        // 2. 数据结构校验
        // 3. 必填字段检查
        // 4. 数据格式验证（身份证、手机号等）
        // 5. 业务逻辑校验（金额、日期等）
    }
    
    // 第二阶段：异步批量入库
    @Async
    @Transactional
    public CompletableFuture<BatchImportResult> batchInsert(List<CaseImportDTO> cases) {
        // 1. 数据分批处理（每批1000条）
        // 2. 使用JdbcTemplate批量插入
        // 3. 敏感数据加密存储
        // 4. 进度反馈通过WebSocket发送
    }
}
```

#### 2. 智能分案算法设计
```java
// 策略模式 + 责任链模式
@Component
public class AssignmentEngine {
    
    @Autowired
    private List<AssignmentStrategy> strategies;
    
    public List<AssignmentRecommendation> recommend(CasePackage casePackage) {
        return strategies.stream()
            .filter(strategy -> strategy.supports(casePackage))
            .flatMap(strategy -> strategy.match(casePackage).stream())
            .sorted(Comparator.comparing(AssignmentRecommendation::getScore).reversed())
            .limit(10)
            .collect(Collectors.toList());
    }
}

// 地域匙配策略
@Component
public class RegionBasedStrategy implements AssignmentStrategy {
    public List<AssignmentRecommendation> match(CasePackage casePackage) {
        // 根据案件地域信息匙配处置机构服务区域
    }
}

// 业绩匙配策略
@Component
public class PerformanceBasedStrategy implements AssignmentStrategy {
    public List<AssignmentRecommendation> match(CasePackage casePackage) {
        // 根据历史业绩数据计算匹配分数
    }
}
```

#### 3. 数据安全处理
```java
// 敏感数据加密工具类
@Service
public class DataEncryptionService {
    
    private final AESUtil aesUtil;
    
    // 身份证加密
    public String encryptIdCard(String idCard) {
        return aesUtil.encrypt(idCard);
    }
    
    // 手机号加密
    public String encryptPhone(String phone) {
        return aesUtil.encrypt(phone);
    }
    
    // 姓名加密
    public String encryptName(String name) {
        return aesUtil.encrypt(name);
    }
}

// 在Entity中使用
@Entity
public class Case {
    @Column(name = "debtor_id_card")
    @Convert(converter = IdCardEncryptConverter.class)
    private String debtorIdCard;
    
    @Column(name = "debtor_name")
    @Convert(converter = NameEncryptConverter.class)
    private String debtorName;
}
```

### 异步处理
```java
// 大批量操作使用异步处理
@Async
@Transactional
public CompletableFuture<BatchImportResult> importCases(MultipartFile file) {
    // 1. 文件解析
    // 2. 数据验证
    // 3. 批量插入
    // 4. 发送通知
}
```

### 测试策略
1. **单元测试**：Service层业务逻辑，覆盖率>80%
2. **集成测试**：API接口测试，MockMvc
3. **性能测试**：JMeter压测关键接口

## 部署架构

### 容器化部署
```yaml
# docker-compose.yml
services:
  nginx:
    # 负载均衡 + 静态资源
  app:
    # Spring Boot应用，多实例
  mysql:
    # 主从配置
  redis:
    # 哨兵模式
```

### 高可用方案
1. **应用层**：多实例 + 负载均衡
2. **数据库**：主从复制 + 读写分离
3. **缓存层**：Redis Sentinel
4. **文件存储**：对象存储 + CDN

## 版本迭代规划

### MVP版本（4-6个月） - 验证商业模式
#### 第一阶段：基础架构（4-6周）
- [x] 项目环境搭建和技术栈配置
- [ ] 微服务基础架构实现
- [ ] 用户认证和权限管理系统
- [ ] 数据库设计和初始化

#### 第二阶段：机构管理（3-4周）
- [ ] 案源机构和处置机构注册入驻
- [ ] 机构资质审核流程
- [ ] 基础权限管理和用户管理
- [ ] 会员费管理系统

#### 第三阶段：案件中心MVP（4-6周）
- [ ] 案件包批量导入功能（Excel/CSV）
- [ ] 案件数据校验和存储（支捘10万+案件）
- [ ] 手动分案功能（无智能匹配）
- [ ] 案件市场基础展示
- [ ] 案件状态流转管理

#### 第四阶段：协同对账（3-4周）
- [ ] 案件详情展示和基础信息管理
- [ ] 回款数据手动录入功能
- [ ] 基础对账功能和报表生成
- [ ] 简单督办提醒功能

#### 第五阶段：报表看板（2-3周）
- [ ] 基础业绩看板（案源方视角）
- [ ] 简单效能看板（处置方视角）
- [ ] 平台运营基础数据展示

### V1.0版本（6-8个月） - 完善用户体验
#### 智能分案引擎（6-8周）
- [ ] 多维度分案策略实现
- [ ] 机器学习推荐算法
- [ ] 智能匹配评分系统
- [ ] 自动分案和推荐模式

#### 在线协议签署（3-4周）
- [ ] 电子合同生成和模板管理
- [ ] 在线签署流程实现
- [ ] 法律效力保障和存证
- [ ] 协议归档管理

#### 智调系统集成（4-6周）
- [ ] API接口标准化和文档
- [ ] 实时数据同步机制
- [ ] 处置进展自动更新
- [ ] 作业数据展示和分析

#### 完善报表系统（4-5周）
- [ ] 多维度数据分析功能
- [ ] 可视化图表升级和定制
- [ ] 自定义报表生成功能
- [ ] 数据导出和分享功能

#### 法律工具服务（5-6周）
- [ ] 法律文书批量生成器
- [ ] 模板管理系统和自定义功能
- [ ] 文书预览和导出功能
- [ ] 立案机器人原型（法院系统对接）

### V2.0版本（持续迭代） - 构建完善生态
#### 债务人互动接口（8-10周）
- [ ] 债务人专属还款页面设计和开发
- [ ] 在线沟通系统实现
- [ ] 支付集成（微信、支付宝、银行卡）
- [ ] 还款方案确认和管理

#### 高级智能服务（持续开发）
- [ ] AI辅助决策系统
- [ ] 大数据分析报告服务
- [ ] 风险评估模型
- [ ] 行业趋势分析和预测

#### 生态扩展（长期规划）
- [ ] 更多第三方系统集成
- [ ] 区块链存证功能
- [ ] 移动端应用开发
- [ ] 开放平台API服务

## 常见问题处理

### 性能瓶颈解决方案
#### 1. 批量导入10万案件性能优化
```java
// 使用JdbcTemplate批量插入
@Service
public class CaseBatchInsertService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public void batchInsert(List<Case> cases) {
        String sql = "INSERT INTO cases_" + getCurrentMonth() + " (...) VALUES (...)";
        
        // 分批处理，每批1000条
        Lists.partition(cases, 1000).forEach(batch -> {
            List<Object[]> batchArgs = batch.stream()
                .map(this::convertToArgs)
                .collect(Collectors.toList());
            
            jdbcTemplate.batchUpdate(sql, batchArgs);
        });
    }
}

// 异步处理 + 进度反馈
@Async
@Transactional
public CompletableFuture<Void> asyncBatchImport(List<CaseImportDTO> importData) {
    int total = importData.size();
    AtomicInteger processed = new AtomicInteger(0);
    
    // 分批处理并发送进度
    Lists.partition(importData, 1000).forEach(batch -> {
        processBatch(batch);
        int current = processed.addAndGet(batch.size());
        
        // WebSocket发送进度
        webSocketService.sendProgress(userId, current * 100 / total);
    });
    
    return CompletableFuture.completedFuture(null);
}
```

#### 2. 查询性能优化
```java
// 使用分库分表策略
@Service
public class CaseQueryService {
    
    // 按月分表查询
    public Page<Case> findCases(CaseQueryDTO query, Pageable pageable) {
        String tableName = determineTableName(query.getDateRange());
        
        // 使用索引优化查询
        String sql = buildOptimizedQuery(tableName, query);
        
        // Redis缓存热点数据
        String cacheKey = buildCacheKey(query, pageable);
        return cacheService.getOrCompute(cacheKey, 
            () -> jdbcTemplate.query(sql, new CaseRowMapper()), 
            Duration.ofMinutes(5));
    }
}

// 索引优化建议
/*
CREATE INDEX idx_case_package_status ON cases_202407(case_package_id, current_status);
CREATE INDEX idx_assigned_org_date ON cases_202407(assigned_org_id, assigned_at);
CREATE INDEX idx_overdue_amount ON cases_202407(overdue_days, remaining_amount);
CREATE INDEX idx_debtor_phone ON cases_202407(debtor_phone); -- 加密后的手机号索引
*/
```

#### 3. 大文件存储优化
```java
// 分片上传实现
@RestController
public class FileUploadController {
    
    @PostMapping("/upload/chunk")
    public ResponseEntity<ChunkUploadResult> uploadChunk(
            @RequestParam("file") MultipartFile chunk,
            @RequestParam("chunkNumber") int chunkNumber,
            @RequestParam("totalChunks") int totalChunks,
            @RequestParam("identifier") String identifier) {
        
        // 保存分片文件
        String chunkPath = saveChunk(identifier, chunkNumber, chunk);
        
        // 检查是否所有分片上传完成
        if (isAllChunksUploaded(identifier, totalChunks)) {
            // 合并分片文件
            String finalPath = mergeChunks(identifier, totalChunks);
            
            // 上传到对象存储（OSS/MinIO）
            String objectUrl = objectStorageService.upload(finalPath);
            
            return ResponseEntity.ok(ChunkUploadResult.completed(objectUrl));
        }
        
        return ResponseEntity.ok(ChunkUploadResult.progress(chunkNumber, totalChunks));
    }
}
```

### 业务逻辑关键实现
#### 1. 案件状态机管理
```java
// 状态机模式管理案件状态流转
@Component
public class CaseStateMachine {
    
    private final Map<CaseStatus, Set<CaseStatus>> transitions = Map.of(
        CaseStatus.PENDING, Set.of(CaseStatus.IN_PROGRESS, CaseStatus.CLOSED),
        CaseStatus.IN_PROGRESS, Set.of(CaseStatus.SETTLED, CaseStatus.LITIGATION, CaseStatus.CLOSED),
        CaseStatus.SETTLED, Set.of(CaseStatus.CLOSED),
        CaseStatus.LITIGATION, Set.of(CaseStatus.CLOSED)
    );
    
    public boolean canTransition(CaseStatus from, CaseStatus to) {
        return transitions.getOrDefault(from, Set.of()).contains(to);
    }
    
    @Transactional
    public void transitionCase(Long caseId, CaseStatus newStatus, String reason) {
        Case caseEntity = caseRepository.findById(caseId)
            .orElseThrow(() -> new CaseNotFoundException(caseId));
            
        if (!canTransition(caseEntity.getCurrentStatus(), newStatus)) {
            throw new IllegalStateTransitionException(
                caseEntity.getCurrentStatus(), newStatus);
        }
        
        // 更新状态并记录历史
        caseEntity.setCurrentStatus(newStatus);
        caseStatusHistoryService.recordTransition(caseId, newStatus, reason);
        
        caseRepository.save(caseEntity);
        
        // 发送状态变更事件
        eventPublisher.publishEvent(new CaseStatusChangedEvent(caseId, newStatus));
    }
}
```

#### 2. 并发分案防重
```java
// 使用Redis分布式锁防止重复分配
@Service
public class AssignmentService {
    
    @Autowired
    private RedissonClient redissonClient;
    
    @Transactional
    public AssignmentResult assignCasePackage(Long packageId, Long disposalOrgId) {
        String lockKey = "assignment:package:" + packageId;
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            // 获取锁，等待10秒，锁定30秒
            if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                
                // 检查案件包是否已被分配
                CasePackage casePackage = casePackageRepository.findById(packageId)
                    .orElseThrow(() -> new CasePackageNotFoundException(packageId));
                    
                if (casePackage.getStatus() != CasePackageStatus.PENDING) {
                    throw new CasePackageAlreadyAssignedException(packageId);
                }
                
                // 执行分配逻辑
                return doAssignment(casePackage, disposalOrgId);
                
            } else {
                throw new AssignmentLockTimeoutException(packageId);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssignmentInterruptedException(packageId);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
```

#### 3. 对账差异处理
```java
// 对账差异检测和处理
@Service
public class ReconciliationService {
    
    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1点执行
    public void dailyReconciliation() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        
        // 获取昨天的所有回款记录
        List<Repayment> repayments = repaymentRepository.findByRepayDate(yesterday);
        
        // 按机构分组进行对账
        Map<Long, List<Repayment>> repaymentsByOrg = repayments.stream()
            .collect(Collectors.groupingBy(Repayment::getSubmittedBy));
            
        repaymentsByOrg.forEach((orgId, orgRepayments) -> {
            ReconciliationResult result = performReconciliation(orgId, orgRepayments);
            
            if (result.hasDiscrepancies()) {
                // 发送差异通知
                notificationService.sendDiscrepancyAlert(orgId, result);
                
                // 创建异议工单
                disputeService.createDispute(orgId, result.getDiscrepancies());
            }
        });
    }
    
    private ReconciliationResult performReconciliation(Long orgId, List<Repayment> repayments) {
        // 对比处置机构提交的数据与系统记录
        // 检查金额、时间、凭证是否一致
        return reconciliationEngine.compare(repayments);
    }
}
```

## 与外部系统集成

### 智调系统（IDS）集成
智调系统是处置机构一线作业人员使用的具体处置工具：

```java
// DRMP与IDS系统集成API接口
@RestController
@RequestMapping("/api/integration/ids")
public class IdsIntegrationController {
    
    // IDS同步案件处置进展
    @PostMapping("/cases/{caseId}/progress")
    public ResponseEntity<Void> syncCaseProgress(
            @PathVariable Long caseId,
            @RequestBody CaseProgressSyncDTO progress) {
        
        caseProgressService.updateProgress(caseId, progress);
        return ResponseEntity.ok().build();
    }
    
    // IDS同步回款数据
    @PostMapping("/cases/{caseId}/repayments")
    public ResponseEntity<Void> syncRepayment(
            @PathVariable Long caseId,
            @RequestBody RepaymentSyncDTO repayment) {
        
        repaymentService.recordRepayment(caseId, repayment);
        return ResponseEntity.ok().build();
    }
    
    // IDS同步沟通记录
    @PostMapping("/cases/{caseId}/communications")
    public ResponseEntity<Void> syncCommunication(
            @PathVariable Long caseId,
            @RequestBody CommunicationRecordDTO communication) {
        
        communicationService.recordCommunication(caseId, communication);
        return ResponseEntity.ok().build();
    }
}

// 为IDS系统提供案件数据查询API
@FeignClient(name = "drmp-client", url = "${drmp.api.base-url}")
public interface DrmpClient {
    
    @GetMapping("/api/cases/{caseId}")
    CaseDetailDTO getCaseDetail(@PathVariable Long caseId);
    
    @GetMapping("/api/organizations/{orgId}/cases")
    Page<CaseDTO> getAssignedCases(
        @PathVariable Long orgId,
        @RequestParam CaseStatus status,
        Pageable pageable);
}
```

### 支付系统集成
支持多种支付方式和场景：

```java
// 统一支付服务
@Service
public class PaymentService {
    
    @Autowired
    private Map<String, PaymentProvider> paymentProviders;
    
    // 会员费支付
    public PaymentResult processMembershipPayment(MembershipPaymentRequest request) {
        PaymentProvider provider = paymentProviders.get(request.getPaymentMethod());
        
        // 创建支付订单
        PaymentOrder order = PaymentOrder.builder()
            .orderId(generateOrderId())
            .amount(request.getAmount())
            .subject("处置机构会员费")
            .payerId(request.getOrganizationId())
            .build();
            
        return provider.createPayment(order);
    }
    
    // 债务人还款（V2.0功能）
    public PaymentResult processDebtorRepayment(DebtorRepaymentRequest request) {
        // 验证债务人身份和欠款信息
        Case caseEntity = validateDebtorIdentity(request);
        
        // 创建还款订单
        PaymentOrder order = PaymentOrder.builder()
            .orderId(generateOrderId())
            .amount(request.getAmount())
            .subject("个人贷款还款")
            .caseId(caseEntity.getId())
            .payerId(request.getDebtorId())
            .build();
            
        PaymentProvider provider = paymentProviders.get(request.getPaymentMethod());
        return provider.createPayment(order);
    }
}

// 支付回调处理
@RestController
@RequestMapping("/api/payment/callback")
public class PaymentCallbackController {
    
    @PostMapping("/alipay")
    public String handleAlipayCallback(HttpServletRequest request) {
        Map<String, String> params = extractParams(request);
        
        if (alipayService.verifySignature(params)) {
            PaymentOrder order = paymentOrderService.findByOrderId(params.get("out_trade_no"));
            paymentOrderService.markAsPaid(order, params);
            
            // 处理业务逻辑
            handlePaymentSuccess(order);
            
            return "success";
        }
        
        return "fail";
    }
}
```

### 法院系统对接
实现批量立案和诉讼进度同步：

```java
// 法院系统集成服务
@Service
public class CourtSystemIntegrationService {
    
    @Autowired
    private CourtApiClient courtApiClient;
    
    // 批量立案
    @Transactional
    public BatchFilingResult batchFiling(List<Long> caseIds) {
        List<Case> cases = caseRepository.findAllById(caseIds);
        
        // 根据法院管辖区域分组
        Map<String, List<Case>> casesByJurisdiction = cases.stream()
            .collect(Collectors.groupingBy(this::determineJurisdiction));
            
        List<FilingResult> results = new ArrayList<>();
        
        casesByJurisdiction.forEach((jurisdiction, jurisdictionCases) -> {
            // 生成法律文书
            List<LegalDocument> documents = legalDocumentService
                .generateFilingDocuments(jurisdictionCases);
                
            // 提交立案申请
            FilingRequest request = FilingRequest.builder()
                .jurisdiction(jurisdiction)
                .cases(jurisdictionCases)
                .documents(documents)
                .build();
                
            FilingResult result = courtApiClient.submitFiling(request);
            results.add(result);
            
            // 更新案件状态
            updateCasesWithFilingResult(jurisdictionCases, result);
        });
        
        return BatchFilingResult.builder()
            .totalCases(cases.size())
            .results(results)
            .build();
    }
    
    // 定期同步诉讼进度
    @Scheduled(cron = "0 0 */4 * * ?") // 每4小时执行一次
    public void syncLitigationProgress() {
        List<Case> litigationCases = caseRepository
            .findByCurrentStatus(CaseStatus.LITIGATION);
            
        litigationCases.forEach(caseEntity -> {
            try {
                LitigationProgress progress = courtApiClient
                    .queryLitigationProgress(caseEntity.getCourtCaseNumber());
                    
                litigationProgressService.updateProgress(caseEntity.getId(), progress);
                
                // 如果案件结案，更新状态
                if (progress.isClosed()) {
                    caseStateMachine.transitionCase(
                        caseEntity.getId(), 
                        CaseStatus.CLOSED, 
                        "法院判决结案");
                }
                
            } catch (Exception e) {
                log.error("同步诉讼进度失败, caseId: {}", caseEntity.getId(), e);
            }
        });
    }
}
```

## 开发工具推荐

### 后端开发
- IDE: IntelliJ IDEA
- API测试: Postman/Insomnia
- 数据库工具: DataGrip/Navicat

### 前端开发
- IDE: VS Code
- 调试工具: React DevTools
- 网络调试: Chrome DevTools

### 协作工具
- 代码管理: Git
- 项目管理: JIRA/禅道
- 文档: Confluence/语雀

## 联系与支持

开发过程中如有疑问，可以：
1. 查阅本文档和PRD
2. 查看代码中的注释和README
3. 联系项目负责人或架构师
4. 在项目群中讨论

## 数据字典参考

基于PRD中的数据字典，以下是核心字段定义：

### 必填字段（核心业务数据）
- **借据编号**：案件唯一标识
- **身份证号**：债务人身份证（加密存储）
- **客户姓名**：债务人姓名（加密存储）
- **手机号**：债务人联系方式（加密存储）
- **借款项目/产品线**：债务所属产品类型
- **贷款金额**：原始贷款金额
- **剩余应还金额**：当前剩余应还总金额
- **逾期天数**：截至导入时的逾期天数
- **委托方**：委托该笔案件的机构名称
- **委托开始时间**：委托处置的开始日期
- **委托到期时间**：委托处置的到期日期
- **资方名称**：资金提供方名称

### 建议填写字段（带*号，用于作业和报表统计）
#### 债务信息
- **总期数**、**月还款额**、**月利率**、**年化利率**
- **放款日**、**贷款到期日**、**还款方式**
- **已还金额**、**逾期日期**、**尚欠利息**
- **罚息利率**、**逾期罚息**、**逾期M值**

#### 债务人信息
- **性别**、**户籍所在省**、**户籍所在市**、**户籍详细地址**
- **现居省**、**现居市**、**现居地址**
- **单位名称**、**债权总额**、**账号**、**开户行**

#### 联系人信息
- 支持1-5个紧急联系人的姓名、电话、关系、工作单位信息

### 自定义字段
- 支持10个自定义字段，由案源机构根据业务需要定义

## 开发最佳实践

1. **数据安全优先**：所有敏感数据必须加密存储，传输加密
2. **性能优先**：考虑大数据量场景，优先使用分批处理、分库分表
3. **异常处理**：完善的异常处理和日志记录，特别是外部系统集成
4. **状态管理**：使用状态机模式管理复杂的业务状态流转
5. **并发控制**：在高并发场景下使用分布式锁防止数据不一致
6. **可观测性**：完善的监控、日志和告警机制
7. **模块化**：遵循微服务边界，保持服务间松耦合

记住：**代码是写给人看的，顺便能被机器执行**。保持代码清晰、文档完善，让项目可持续发展。