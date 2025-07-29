-- DRMP平台基础数据初始化脚本
-- 版本：1.0.0
-- 创建时间：2024-07-29

SET NAMES utf8mb4;

-- ----------------------------
-- 1. 插入系统角色数据
-- ----------------------------
INSERT INTO `roles` (`id`, `name`, `code`, `description`, `org_type`, `is_default`, `sort_order`) VALUES
(1, '平台超级管理员', 'PLATFORM_SUPER_ADMIN', '平台最高权限管理员', 'PLATFORM', 0, 1),
(2, '平台管理员', 'PLATFORM_ADMIN', '平台管理员，负责机构审核等', 'PLATFORM', 1, 2),
(3, '平台运营', 'PLATFORM_OPERATOR', '平台运营人员', 'PLATFORM', 0, 3),
(4, '案源机构管理员', 'SOURCE_ADMIN', '案源机构管理员', 'SOURCE', 1, 4),
(5, '案源机构用户', 'SOURCE_USER', '案源机构普通用户', 'SOURCE', 0, 5),
(6, '处置机构管理员', 'DISPOSAL_ADMIN', '处置机构管理员', 'DISPOSAL', 1, 6),
(7, '处置机构用户', 'DISPOSAL_USER', '处置机构普通用户', 'DISPOSAL', 0, 7);

-- ----------------------------
-- 2. 插入系统权限数据
-- ----------------------------
INSERT INTO `permissions` (`id`, `name`, `code`, `type`, `parent_id`, `path`, `method`, `icon`, `sort_order`, `description`) VALUES
-- 机构管理权限
(1, '机构管理', 'ORG_MANAGE', 'MENU', 0, '/organization', NULL, 'BuildOutlined', 1, '机构管理菜单'),
(2, '机构列表', 'ORG_LIST', 'MENU', 1, '/organization/list', NULL, 'UnorderedListOutlined', 1, '机构列表页面'),
(3, '机构详情', 'ORG_VIEW', 'BUTTON', 2, NULL, NULL, 'EyeOutlined', 1, '查看机构详情'),
(4, '机构审核', 'ORG_AUDIT', 'BUTTON', 2, NULL, NULL, 'AuditOutlined', 2, '机构审核权限'),
(5, '机构编辑', 'ORG_EDIT', 'BUTTON', 2, NULL, NULL, 'EditOutlined', 3, '编辑机构信息'),
(6, '机构删除', 'ORG_DELETE', 'BUTTON', 2, NULL, NULL, 'DeleteOutlined', 4, '删除机构'),

-- 案件管理权限
(10, '案件管理', 'CASE_MANAGE', 'MENU', 0, '/case', NULL, 'FileTextOutlined', 2, '案件管理菜单'),
(11, '案件包列表', 'CASE_PACKAGE_LIST', 'MENU', 10, '/case/package', NULL, 'FolderOutlined', 1, '案件包列表'),
(12, '案件包详情', 'CASE_PACKAGE_VIEW', 'BUTTON', 11, NULL, NULL, 'EyeOutlined', 1, '查看案件包详情'),
(13, '发布案件包', 'CASE_PACKAGE_PUBLISH', 'BUTTON', 11, NULL, NULL, 'CloudUploadOutlined', 2, '发布案件包'),
(14, '案件包导入', 'CASE_PACKAGE_IMPORT', 'BUTTON', 11, NULL, NULL, 'ImportOutlined', 3, '批量导入案件'),
(15, '案件包编辑', 'CASE_PACKAGE_EDIT', 'BUTTON', 11, NULL, NULL, 'EditOutlined', 4, '编辑案件包'),
(16, '案件包删除', 'CASE_PACKAGE_DELETE', 'BUTTON', 11, NULL, NULL, 'DeleteOutlined', 5, '删除案件包'),
(17, '案件列表', 'CASE_LIST', 'MENU', 10, '/case/list', NULL, 'UnorderedListOutlined', 2, '案件列表'),
(18, '案件详情', 'CASE_VIEW', 'BUTTON', 17, NULL, NULL, 'EyeOutlined', 1, '查看案件详情'),
(19, '案件编辑', 'CASE_EDIT', 'BUTTON', 17, NULL, NULL, 'EditOutlined', 2, '编辑案件信息'),

-- 分案管理权限
(20, '分案管理', 'ASSIGNMENT_MANAGE', 'MENU', 0, '/assignment', NULL, 'DeploymentUnitOutlined', 3, '分案管理菜单'),
(21, '分案列表', 'ASSIGNMENT_LIST', 'MENU', 20, '/assignment/list', NULL, 'UnorderedListOutlined', 1, '分案记录列表'),
(22, '手动分案', 'ASSIGNMENT_MANUAL', 'BUTTON', 21, NULL, NULL, 'UserSwitchOutlined', 1, '手动分案'),
(23, '智能分案', 'ASSIGNMENT_INTELLIGENT', 'BUTTON', 21, NULL, NULL, 'RobotOutlined', 2, '智能分案'),
(24, '分案审核', 'ASSIGNMENT_AUDIT', 'BUTTON', 21, NULL, NULL, 'AuditOutlined', 3, '分案审核'),
(25, '分案撤回', 'ASSIGNMENT_CANCEL', 'BUTTON', 21, NULL, NULL, 'RollbackOutlined', 4, '撤回分案'),

-- 用户管理权限
(30, '用户管理', 'USER_MANAGE', 'MENU', 0, '/user', NULL, 'UserOutlined', 4, '用户管理菜单'),
(31, '用户列表', 'USER_LIST', 'MENU', 30, '/user/list', NULL, 'UnorderedListOutlined', 1, '用户列表'),
(32, '用户详情', 'USER_VIEW', 'BUTTON', 31, NULL, NULL, 'EyeOutlined', 1, '查看用户详情'),
(33, '用户新增', 'USER_ADD', 'BUTTON', 31, NULL, NULL, 'PlusOutlined', 2, '新增用户'),
(34, '用户编辑', 'USER_EDIT', 'BUTTON', 31, NULL, NULL, 'EditOutlined', 3, '编辑用户'),
(35, '用户删除', 'USER_DELETE', 'BUTTON', 31, NULL, NULL, 'DeleteOutlined', 4, '删除用户'),
(36, '重置密码', 'USER_RESET_PASSWORD', 'BUTTON', 31, NULL, NULL, 'KeyOutlined', 5, '重置用户密码'),

-- 系统管理权限
(40, '系统管理', 'SYSTEM_MANAGE', 'MENU', 0, '/system', NULL, 'SettingOutlined', 5, '系统管理菜单'),
(41, '角色管理', 'ROLE_MANAGE', 'MENU', 40, '/system/role', NULL, 'TeamOutlined', 1, '角色管理'),
(42, '权限管理', 'PERMISSION_MANAGE', 'MENU', 40, '/system/permission', NULL, 'SafetyOutlined', 2, '权限管理'),
(43, '系统配置', 'CONFIG_MANAGE', 'MENU', 40, '/system/config', NULL, 'ControlOutlined', 3, '系统配置'),

-- API权限
(50, '机构API', 'API_ORG', 'API', 0, '/api/v1/organizations/**', 'ALL', NULL, 1, '机构相关API'),
(51, '案件API', 'API_CASE', 'API', 0, '/api/v1/cases/**', 'ALL', NULL, 2, '案件相关API'),
(52, '分案API', 'API_ASSIGNMENT', 'API', 0, '/api/v1/assignments/**', 'ALL', NULL, 3, '分案相关API'),
(53, '用户API', 'API_USER', 'API', 0, '/api/v1/users/**', 'ALL', NULL, 4, '用户相关API'),
(54, '系统API', 'API_SYSTEM', 'API', 0, '/api/v1/system/**', 'ALL', NULL, 5, '系统相关API');

-- ----------------------------
-- 3. 插入角色权限关联数据
-- ----------------------------
-- 平台超级管理员（所有权限）
INSERT INTO `role_permissions` (`role_id`, `permission_id`) 
SELECT 1, `id` FROM `permissions`;

-- 平台管理员（机构审核、用户管理、系统配置）
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5),  -- 机构管理
(2, 30), (2, 31), (2, 32), (2, 33), (2, 34), (2, 35), (2, 36),  -- 用户管理
(2, 40), (2, 41), (2, 42), (2, 43),  -- 系统管理
(2, 50), (2, 53), (2, 54);  -- 相关API

-- 平台运营（查看权限为主）
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES
(3, 1), (3, 2), (3, 3),  -- 机构查看
(3, 10), (3, 11), (3, 12), (3, 17), (3, 18),  -- 案件查看
(3, 20), (3, 21),  -- 分案查看
(3, 50), (3, 51), (3, 52);  -- 相关API

-- 案源机构管理员（案件管理、分案管理）
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES
(4, 10), (4, 11), (4, 12), (4, 13), (4, 14), (4, 15), (4, 16),  -- 案件包管理
(4, 17), (4, 18), (4, 19),  -- 案件管理
(4, 20), (4, 21), (4, 22), (4, 25),  -- 分案管理
(4, 30), (4, 31), (4, 32), (4, 33), (4, 34), (4, 35), (4, 36),  -- 机构内用户管理
(4, 51), (4, 52), (4, 53);  -- 相关API

-- 案源机构用户（查看和基础操作）
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES
(5, 10), (5, 11), (5, 12), (5, 17), (5, 18),  -- 案件查看
(5, 20), (5, 21),  -- 分案查看
(5, 51), (5, 52);  -- 相关API

-- 处置机构管理员（分案接受、案件处理）
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES
(6, 10), (6, 17), (6, 18), (6, 19),  -- 案件查看和处理
(6, 20), (6, 21), (6, 24),  -- 分案审核
(6, 30), (6, 31), (6, 32), (6, 33), (6, 34), (6, 35), (6, 36),  -- 机构内用户管理
(6, 51), (6, 52), (6, 53);  -- 相关API

-- 处置机构用户（案件处理）
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES
(7, 10), (7, 17), (7, 18), (7, 19),  -- 案件查看和处理
(7, 20), (7, 21),  -- 分案查看
(7, 51), (7, 52);  -- 相关API

-- ----------------------------
-- 4. 插入系统配置数据
-- ----------------------------
INSERT INTO `system_configs` (`config_key`, `config_value`, `config_type`, `description`, `is_public`, `sort_order`) VALUES
-- 文件上传配置
('file.upload.maxSize', '104857600', 'NUMBER', '文件上传最大大小（字节）：100MB', 1, 1),
('file.upload.allowedTypes', '["xlsx","xls","csv"]', 'JSON', '允许上传的文件类型', 1, 2),
('file.upload.path', '/uploads', 'STRING', '文件上传路径', 0, 3),

-- 批量导入配置
('import.batch.size', '1000', 'NUMBER', '批量导入每批处理数量', 0, 10),
('import.max.records', '100000', 'NUMBER', '单次导入最大记录数', 1, 11),
('import.timeout', '300', 'NUMBER', '导入超时时间（秒）', 0, 12),

-- 分案配置
('assignment.expire.hours', '72', 'NUMBER', '分案过期时间（小时）', 1, 20),
('assignment.auto.accept', 'false', 'BOOLEAN', '是否启用自动接受分案', 1, 21),

-- 系统配置
('system.name', 'DRMP全国分散诉调平台', 'STRING', '系统名称', 1, 30),
('system.version', '1.0.0', 'STRING', '系统版本', 1, 31),
('system.copyright', '© 2024 DRMP Team', 'STRING', '版权信息', 1, 32),

-- 安全配置
('security.password.minLength', '8', 'NUMBER', '密码最小长度', 1, 40),
('security.password.requireSpecial', 'true', 'BOOLEAN', '密码是否需要特殊字符', 1, 41),
('security.session.timeout', '7200', 'NUMBER', '会话超时时间（秒）', 0, 42),
('security.login.maxAttempts', '5', 'NUMBER', '登录最大尝试次数', 0, 43),

-- 加密配置
('encryption.algorithm', 'AES', 'STRING', '数据加密算法', 0, 50),
('encryption.keySize', '256', 'NUMBER', '加密密钥长度', 0, 51);

-- ----------------------------
-- 5. 创建默认平台管理员账号
-- ----------------------------
-- 插入平台机构（用于平台管理员）
INSERT INTO `organizations` (`id`, `name`, `type`, `status`, `contact_person`, `contact_email`, `description`) VALUES
(1, 'DRMP平台', 'SOURCE', 'ACTIVE', '系统管理员', 'admin@drmp.com', 'DRMP平台官方机构');

-- 插入平台管理员用户（密码：admin123，需要实际使用时加密）
INSERT INTO `users` (`id`, `username`, `password`, `nickname`, `real_name`, `email`, `org_id`, `status`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyYgGTT9yQk7BbUYmYtjM..Y2TS', '平台管理员', '系统管理员', 'admin@drmp.com', 1, 'ACTIVE');

-- 给管理员分配角色
INSERT INTO `user_roles` (`user_id`, `role_id`) VALUES (1, 1);

-- ----------------------------
-- 6. 创建示例机构数据（用于测试）
-- ----------------------------
-- 案源机构示例
INSERT INTO `organizations` (`name`, `type`, `sub_type`, `status`, `contact_person`, `contact_phone`, `contact_email`, `address`, `team_size`, `description`) VALUES
('XX银行', 'SOURCE', '商业银行', 'ACTIVE', '张经理', 'encrypted_phone_1', 'zhang@xxbank.com', '北京市朝阳区XX路123号', NULL, '大型股份制商业银行'),
('XX消费金融', 'SOURCE', '消费金融公司', 'ACTIVE', '李经理', 'encrypted_phone_2', 'li@xxcf.com', '上海市浦东新区XX大厦', NULL, '专业消费金融服务机构');

-- 处置机构示例
INSERT INTO `organizations` (`name`, `type`, `sub_type`, `status`, `contact_person`, `contact_phone`, `contact_email`, `address`, `team_size`, `monthly_capacity`, `service_regions`, `business_scope`, `disposal_types`, `description`) VALUES
('XX律师事务所', 'DISPOSAL', '律师事务所', 'ACTIVE', '王律师', 'encrypted_phone_3', 'wang@xxlaw.com', '北京市海淀区XX广场', 50, 5000, '["北京", "河北", "天津"]', '["民商事诉讼", "金融纠纷"]', '["调解", "诉讼"]', '专业金融法律服务机构'),
('XX调解中心', 'DISPOSAL', '调解中心', 'ACTIVE', '赵主任', 'encrypted_phone_4', 'zhao@xxmediation.com', '广州市天河区XX中心', 30, 3000, '["广东", "广西", "海南"]', '["金融调解", "民事调解"]', '["调解", "催收"]', '专业金融纠纷调解机构');

-- 为示例机构创建管理员用户
INSERT INTO `users` (`username`, `password`, `nickname`, `real_name`, `email`, `org_id`, `status`) VALUES
('bank_admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyYgGTT9yQk7BbUYmYtjM..Y2TS', '银行管理员', '张经理', 'zhang@xxbank.com', 2, 'ACTIVE'),
('cf_admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyYgGTT9yQk7BbUYmYtjM..Y2TS', '消金管理员', '李经理', 'li@xxcf.com', 3, 'ACTIVE'),
('law_admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyYgGTT9yQk7BbUYmYtjM..Y2TS', '律所管理员', '王律师', 'wang@xxlaw.com', 4, 'ACTIVE'),
('mediation_admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyYgGTT9yQk7BbUYmYtjM..Y2TS', '调解管理员', '赵主任', 'zhao@xxmediation.com', 5, 'ACTIVE');

-- 分配角色
INSERT INTO `user_roles` (`user_id`, `role_id`) VALUES
(2, 4), -- 银行管理员 -> 案源机构管理员
(3, 4), -- 消金管理员 -> 案源机构管理员  
(4, 6), -- 律所管理员 -> 处置机构管理员
(5, 6); -- 调解管理员 -> 处置机构管理员