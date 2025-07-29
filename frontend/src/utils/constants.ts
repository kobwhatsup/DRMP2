/**
 * 前端常量定义
 */

// 机构类型
export const ORGANIZATION_TYPES = {
  SOURCE: '案源机构',
  DISPOSAL: '处置机构',
} as const;

// 案件状态
export const CASE_STATUS = {
  PENDING_ASSIGNMENT: '待分案',
  ASSIGNED: '已分案',
  PROCESSING: '处置中',
  MEDIATING: '调解中',
  LITIGATING: '诉讼中',
  SETTLED: '已和解',
  CLOSED: '已结案',
  WITHDRAWN: '已撤回',
  SUSPENDED: '已暂停',
} as const;

// 案件包状态
export const CASE_PACKAGE_STATUS = {
  DRAFT: '草稿',
  PUBLISHED: '已发布',
  PROCESSING: '处置中',
  COMPLETED: '已完成',
} as const;

// 机构状态
export const ORGANIZATION_STATUS = {
  PENDING: '待审核',
  ACTIVE: '正常',
  SUSPENDED: '停用',
} as const;

// 分页配置
export const PAGINATION = {
  DEFAULT_PAGE_SIZE: 10,
  PAGE_SIZE_OPTIONS: ['10', '20', '50', '100'],
  SHOW_SIZE_CHANGER: true,
  SHOW_QUICK_JUMPER: true,
  SHOW_TOTAL: (total: number, range: [number, number]) =>
    `第 ${range[0]}-${range[1]} 条/总共 ${total} 条`,
} as const;

// 上传配置
export const UPLOAD_CONFIG = {
  MAX_FILE_SIZE: 100 * 1024 * 1024, // 100MB
  ALLOWED_TYPES: [
    'image/jpeg',
    'image/png',
    'image/gif',
    'application/pdf',
    'application/msword',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    'application/vnd.ms-excel',
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
  ],
  ACCEPT: '.jpg,.jpeg,.png,.gif,.pdf,.doc,.docx,.xls,.xlsx',
} as const;

// 表单验证规则
export const VALIDATION_RULES = {
  REQUIRED: { required: true, message: '此字段为必填项' },
  PHONE: {
    pattern: /^1[3-9]\d{9}$/,
    message: '请输入正确的手机号码',
  },
  EMAIL: {
    type: 'email' as const,
    message: '请输入正确的邮箱地址',
  },
  ID_CARD: {
    pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/,
    message: '请输入正确的身份证号码',
  },
  PASSWORD: {
    min: 6,
    max: 20,
    message: '密码长度应为6-20位字符',
  },
} as const;

// 菜单权限
export const MENU_PERMISSIONS = {
  // 机构管理
  ORG_MANAGEMENT: 'org:management',
  ORG_LIST: 'org:list',
  ORG_CREATE: 'org:create',
  ORG_UPDATE: 'org:update',
  ORG_DELETE: 'org:delete',
  
  // 案件管理
  CASE_MANAGEMENT: 'case:management',
  CASE_LIST: 'case:list',
  CASE_CREATE: 'case:create',
  CASE_UPDATE: 'case:update',
  CASE_DELETE: 'case:delete',
  CASE_IMPORT: 'case:import',
  CASE_EXPORT: 'case:export',
  
  // 分案管理
  ASSIGNMENT_MANAGEMENT: 'assignment:management',
  ASSIGNMENT_AUTO: 'assignment:auto',
  ASSIGNMENT_MANUAL: 'assignment:manual',
  
  // 数据报表
  REPORT_MANAGEMENT: 'report:management',
  REPORT_PERFORMANCE: 'report:performance',
  REPORT_EFFICIENCY: 'report:efficiency',
  
  // 系统管理
  SYSTEM_MANAGEMENT: 'system:management',
  USER_MANAGEMENT: 'user:management',
  ROLE_MANAGEMENT: 'role:management',
  PERMISSION_MANAGEMENT: 'permission:management',
} as const;