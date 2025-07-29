/**
 * API响应通用类型定义
 */

export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
  timestamp: string;
  traceId?: string;
}

export interface PageResult<T = any> {
  records: T[];
  total: number;
  current: number;
  size: number;
  pages: number;
}

export interface PageParams {
  current?: number;
  size?: number;
  [key: string]: any;
}

export interface LoginRequest {
  username: string;
  password: string;
  captcha?: string;
  rememberMe?: boolean;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  userInfo: UserInfo;
}

export interface UserInfo {
  id: number;
  username: string;
  nickname: string;
  avatar?: string;
  email?: string;
  phone?: string;
  orgId: number;
  orgName: string;
  orgType: 'SOURCE' | 'DISPOSAL';
  roles: string[];
  permissions: string[];
}