-- DRMP平台数据库初始化脚本
-- 版本：1.0.0
-- 创建时间：2024-07-29

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 机构表 - organizations
-- ----------------------------
DROP TABLE IF EXISTS `organizations`;
CREATE TABLE `organizations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(255) NOT NULL COMMENT '机构名称',
  `type` enum('SOURCE','DISPOSAL') NOT NULL COMMENT '机构类型：SOURCE-案源机构，DISPOSAL-处置机构',
  `sub_type` varchar(50) DEFAULT NULL COMMENT '子类型：银行/消金/网贷/律所/调解中心等',
  `status` enum('PENDING','ACTIVE','SUSPENDED','REJECTED') DEFAULT 'PENDING' COMMENT '状态：待审核/活跃/暂停/拒绝',
  `contact_person` varchar(100) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(255) DEFAULT NULL COMMENT '联系电话（加密存储）',
  `contact_email` varchar(100) DEFAULT NULL COMMENT '联系邮箱',
  `address` text COMMENT '办公地址',
  `business_license` varchar(500) DEFAULT NULL COMMENT '营业执照文件路径',
  `legal_person` varchar(100) DEFAULT NULL COMMENT '法定代表人',
  `unified_credit_code` varchar(50) DEFAULT NULL COMMENT '统一社会信用代码',
  `registration_capital` decimal(15,2) DEFAULT NULL COMMENT '注册资本（万元）',
  `establish_date` date DEFAULT NULL COMMENT '成立日期',
  -- 处置机构特有字段
  `team_size` int(11) DEFAULT NULL COMMENT '团队规模（人数）',
  `monthly_capacity` int(11) DEFAULT NULL COMMENT '月处理案件能力',
  `current_load` varchar(50) DEFAULT NULL COMMENT '当前负载：LOW/MEDIUM/HIGH',
  `service_regions` json DEFAULT NULL COMMENT '服务区域列表',
  `business_scope` json DEFAULT NULL COMMENT '业务范围',
  `disposal_types` json DEFAULT NULL COMMENT '处置类型：调解/诉讼/催收等',
  `settlement_methods` json DEFAULT NULL COMMENT '结算方式：全风险/半风险/无风险',
  `cooperation_cases` text COMMENT '合作案例描述',
  `description` text COMMENT '机构描述',
  -- 审核相关
  `audit_status` enum('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING' COMMENT '审核状态',
  `audit_comment` text COMMENT '审核意见',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `audit_by` bigint(20) DEFAULT NULL COMMENT '审核人ID',
  -- 合同相关
  `contract_start_date` date DEFAULT NULL COMMENT '合同开始日期',
  `contract_end_date` date DEFAULT NULL COMMENT '合同结束日期',
  `contract_file` varchar(500) DEFAULT NULL COMMENT '合同文件路径',
  -- 基础字段
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  UNIQUE KEY `uk_credit_code` (`unified_credit_code`),
  KEY `idx_type_status` (`type`,`status`),
  KEY `idx_audit_status` (`audit_status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_regions` ((cast(`service_regions` as char(255) array)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='机构信息表';

-- ----------------------------
-- 2. 用户表 - users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码（加密）',
  `nickname` varchar(100) DEFAULT NULL COMMENT '昵称',
  `real_name` varchar(100) DEFAULT NULL COMMENT '真实姓名',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(50) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(500) DEFAULT NULL COMMENT '头像URL',
  `org_id` bigint(20) NOT NULL COMMENT '所属机构ID',
  `status` enum('ACTIVE','DISABLED','LOCKED') DEFAULT 'ACTIVE' COMMENT '状态：活跃/禁用/锁定',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(50) DEFAULT NULL COMMENT '最后登录IP',
  `login_count` int(11) DEFAULT '0' COMMENT '登录次数',
  `password_update_time` datetime DEFAULT NULL COMMENT '密码更新时间',
  `mfa_enabled` tinyint(1) DEFAULT '0' COMMENT '是否启用多因子认证',
  `mfa_secret` varchar(255) DEFAULT NULL COMMENT 'MFA密钥',
  -- 基础字段
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_org_id` (`org_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_users_org_id` FOREIGN KEY (`org_id`) REFERENCES `organizations` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表';

-- ----------------------------
-- 3. 角色表 - roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '角色名称',
  `code` varchar(50) NOT NULL COMMENT '角色编码',
  `description` varchar(255) DEFAULT NULL COMMENT '角色描述',
  `org_type` enum('SOURCE','DISPOSAL','PLATFORM') DEFAULT NULL COMMENT '适用机构类型',
  `is_default` tinyint(1) DEFAULT '0' COMMENT '是否默认角色',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序序号',
  -- 基础字段
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_org_type` (`org_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- ----------------------------
-- 4. 权限表 - permissions
-- ----------------------------
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '权限名称',
  `code` varchar(100) NOT NULL COMMENT '权限编码',
  `type` enum('MENU','BUTTON','API') NOT NULL COMMENT '权限类型：菜单/按钮/API',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父权限ID',
  `path` varchar(255) DEFAULT NULL COMMENT '菜单路径或API路径',
  `method` varchar(10) DEFAULT NULL COMMENT 'HTTP方法',
  `icon` varchar(100) DEFAULT NULL COMMENT '图标',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序序号',
  `description` varchar(255) DEFAULT NULL COMMENT '权限描述',
  -- 基础字段
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- ----------------------------
-- 5. 用户角色关联表 - user_roles
-- ----------------------------
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`),
  CONSTRAINT `fk_user_roles_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_user_roles_role_id` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- ----------------------------
-- 6. 角色权限关联表 - role_permissions
-- ----------------------------
DROP TABLE IF EXISTS `role_permissions`;
CREATE TABLE `role_permissions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `permission_id` bigint(20) NOT NULL COMMENT '权限ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_id` (`permission_id`),
  CONSTRAINT `fk_role_permissions_role_id` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`),
  CONSTRAINT `fk_role_permissions_permission_id` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- ----------------------------
-- 7. 案件包表 - case_packages
-- ----------------------------
DROP TABLE IF EXISTS `case_packages`;
CREATE TABLE `case_packages` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(255) NOT NULL COMMENT '案件包名称',
  `description` text COMMENT '案件包描述',
  `source_org_id` bigint(20) NOT NULL COMMENT '案源机构ID',
  `total_count` int(11) NOT NULL DEFAULT '0' COMMENT '案件总数量',
  `total_amount` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT '案件总金额',
  `assigned_count` int(11) NOT NULL DEFAULT '0' COMMENT '已分案数量',
  `assigned_amount` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT '已分案金额',
  `status` enum('DRAFT','PUBLISHED','PROCESSING','COMPLETED','WITHDRAWN') DEFAULT 'DRAFT' COMMENT '状态：草稿/已发布/处理中/已完成/已撤回',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `expected_recovery_rate` decimal(5,2) DEFAULT NULL COMMENT '期望回款率（%）',
  `expected_period` int(11) DEFAULT NULL COMMENT '期望处置周期（天）',
  `preferred_methods` json DEFAULT NULL COMMENT '偏好处置方式',
  `assignment_strategy` json DEFAULT NULL COMMENT '分案策略配置',
  `import_file_path` varchar(500) DEFAULT NULL COMMENT '导入文件路径',
  `import_status` enum('PENDING','PROCESSING','SUCCESS','FAILED') DEFAULT 'PENDING' COMMENT '导入状态',
  `import_progress` int(11) DEFAULT '0' COMMENT '导入进度（%）',
  `import_error_msg` text COMMENT '导入错误信息',
  -- 基础字段
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  KEY `idx_source_org_status` (`source_org_id`,`status`),
  KEY `idx_publish_time` (`publish_time`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_import_status` (`import_status`),
  CONSTRAINT `fk_case_packages_source_org_id` FOREIGN KEY (`source_org_id`) REFERENCES `organizations` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='案件包表';

-- ----------------------------
-- 8. 案件表 - cases_template（模板表，实际按月分表）
-- ----------------------------
DROP TABLE IF EXISTS `cases_template`;
CREATE TABLE `cases_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `case_package_id` bigint(20) NOT NULL COMMENT '所属案件包ID',
  -- 必填字段（来自PRD数据字典）
  `receipt_number` varchar(100) NOT NULL COMMENT '借据编号（唯一标识）',
  `debtor_id_card` varchar(255) NOT NULL COMMENT '身份证号（加密存储）',
  `debtor_name` varchar(255) NOT NULL COMMENT '客户姓名（加密存储）',
  `debtor_phone` varchar(255) NOT NULL COMMENT '手机号（加密存储）',
  `loan_product` varchar(100) NOT NULL COMMENT '借款项目/产品线',
  `loan_amount` decimal(15,2) NOT NULL COMMENT '贷款金额',
  `remaining_amount` decimal(15,2) NOT NULL COMMENT '剩余应还金额',
  `overdue_days` int(11) NOT NULL COMMENT '逾期天数',
  `consigner` varchar(100) NOT NULL COMMENT '委托方',
  `consign_start_date` date NOT NULL COMMENT '委托开始时间',
  `consign_end_date` date NOT NULL COMMENT '委托到期时间',
  `fund_provider` varchar(100) NOT NULL COMMENT '资方名称',
  -- 建议填写字段（存储为JSON，支持灵活扩展）
  `debt_info` json DEFAULT NULL COMMENT '债务信息（总期数、月还款额、利率等）',
  `debtor_info` json DEFAULT NULL COMMENT '债务人信息（性别、户籍、现居地址等）',
  `contact_info` json DEFAULT NULL COMMENT '联系人信息（1-5个紧急联系人）',
  `custom_fields` json DEFAULT NULL COMMENT '自定义字段（支持10个）',
  -- 案件状态与处置信息
  `current_status` enum('PENDING_ASSIGNMENT','ASSIGNED','PROCESSING','MEDIATING','LITIGATING','SETTLED','CLOSED','WITHDRAWN','SUSPENDED') DEFAULT 'PENDING_ASSIGNMENT' COMMENT '当前状态',
  `assigned_org_id` bigint(20) DEFAULT NULL COMMENT '分配的处置机构ID',
  `assigned_at` datetime DEFAULT NULL COMMENT '分配时间',
  `latest_progress` text COMMENT '最新处置进展',
  `total_recovered` decimal(15,2) DEFAULT '0.00' COMMENT '已回款金额',
  `recovery_rate` decimal(5,2) DEFAULT '0.00' COMMENT '回款率（%）',
  -- 文件附件
  `attachments` json DEFAULT NULL COMMENT '案件凭证文件列表',
  -- 基础字段
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_receipt_number` (`receipt_number`),
  KEY `idx_case_package_status` (`case_package_id`,`current_status`),
  KEY `idx_assigned_org` (`assigned_org_id`),
  KEY `idx_overdue_days` (`overdue_days`),
  KEY `idx_remaining_amount` (`remaining_amount`),
  KEY `idx_assigned_at` (`assigned_at`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_cases_case_package_id` FOREIGN KEY (`case_package_id`) REFERENCES `case_packages` (`id`),
  CONSTRAINT `fk_cases_assigned_org_id` FOREIGN KEY (`assigned_org_id`) REFERENCES `organizations` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='案件表模板（实际使用时按月分表）';

-- ----------------------------
-- 9. 分案记录表 - assignments
-- ----------------------------
DROP TABLE IF EXISTS `assignments`;
CREATE TABLE `assignments` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `case_package_id` bigint(20) NOT NULL COMMENT '案件包ID',
  `source_org_id` bigint(20) NOT NULL COMMENT '案源机构ID',
  `disposal_org_id` bigint(20) NOT NULL COMMENT '处置机构ID',
  `case_count` int(11) NOT NULL COMMENT '分配案件数量',
  `total_amount` decimal(15,2) NOT NULL COMMENT '分配案件总金额',
  `assignment_type` enum('MANUAL','INTELLIGENT','AUTO_ACCEPT') NOT NULL COMMENT '分案类型：手动/智能/自动接受',
  `strategy_used` varchar(100) DEFAULT NULL COMMENT '使用的分案策略',
  `match_score` decimal(5,2) DEFAULT NULL COMMENT '匹配分数（智能分案）',
  `status` enum('PENDING','ACCEPTED','REJECTED','EXPIRED','CANCELLED') DEFAULT 'PENDING' COMMENT '状态：待处理/已接受/已拒绝/已过期/已取消',
  `assigned_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分案时间',
  `expire_at` datetime DEFAULT NULL COMMENT '过期时间',
  `responded_at` datetime DEFAULT NULL COMMENT '响应时间',
  `reject_reason` text COMMENT '拒绝原因',
  `settlement_method` enum('FULL_RISK','HALF_RISK','NO_RISK') DEFAULT NULL COMMENT '结算方式：全风险/半风险/无风险',
  `commission_rate` decimal(5,2) DEFAULT NULL COMMENT '佣金比例（%）',
  `fixed_fee` decimal(10,2) DEFAULT NULL COMMENT '固定费用',
  `notes` text COMMENT '备注信息',
  -- 基础字段
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  KEY `idx_case_package_status` (`case_package_id`,`status`),
  KEY `idx_disposal_org_status` (`disposal_org_id`,`status`),
  KEY `idx_assigned_at` (`assigned_at`),
  KEY `idx_expire_at` (`expire_at`),
  CONSTRAINT `fk_assignments_case_package_id` FOREIGN KEY (`case_package_id`) REFERENCES `case_packages` (`id`),
  CONSTRAINT `fk_assignments_source_org_id` FOREIGN KEY (`source_org_id`) REFERENCES `organizations` (`id`),
  CONSTRAINT `fk_assignments_disposal_org_id` FOREIGN KEY (`disposal_org_id`) REFERENCES `organizations` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分案记录表';

-- ----------------------------
-- 10. 系统配置表 - system_configs
-- ----------------------------
DROP TABLE IF EXISTS `system_configs`;
CREATE TABLE `system_configs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` varchar(100) NOT NULL COMMENT '配置键',
  `config_value` text COMMENT '配置值',
  `config_type` varchar(50) DEFAULT 'STRING' COMMENT '配置类型：STRING/NUMBER/BOOLEAN/JSON',
  `description` varchar(255) DEFAULT NULL COMMENT '配置描述',
  `is_public` tinyint(1) DEFAULT '0' COMMENT '是否公开配置',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序序号',
  -- 基础字段
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  KEY `idx_config_type` (`config_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- ----------------------------
-- 创建当前月份的案件表
-- ----------------------------
SET @current_month = DATE_FORMAT(NOW(), '%Y%m');
SET @sql = CONCAT('CREATE TABLE cases_', @current_month, ' LIKE cases_template');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET FOREIGN_KEY_CHECKS = 1;