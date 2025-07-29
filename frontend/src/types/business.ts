/**
 * 业务相关类型定义
 */

export type OrganizationType = 'SOURCE' | 'DISPOSAL';
export type CaseStatus = 
  | 'PENDING_ASSIGNMENT' 
  | 'ASSIGNED' 
  | 'PROCESSING' 
  | 'MEDIATING' 
  | 'LITIGATING' 
  | 'SETTLED' 
  | 'CLOSED' 
  | 'WITHDRAWN' 
  | 'SUSPENDED';

export interface Organization {
  id: number;
  name: string;
  type: OrganizationType;
  status: 'PENDING' | 'ACTIVE' | 'SUSPENDED';
  contactPerson: string;
  contactPhone: string;
  contactEmail: string;
  address: string;
  businessLicense: string;
  createTime: string;
  updateTime: string;
}

export interface CaseInfo {
  id: number;
  casePackageId: number;
  debtorName: string;
  debtorIdCard: string;
  debtorPhone: string;
  loanAmount: number;
  remainingAmount: number;
  overdueDays: number;
  status: CaseStatus;
  sourceOrgId: number;
  sourceOrgName: string;
  disposalOrgId?: number;
  disposalOrgName?: string;
  assignTime?: string;
  createTime: string;
  updateTime: string;
}

export interface CasePackage {
  id: number;
  name: string;
  description?: string;
  sourceOrgId: number;
  sourceOrgName: string;
  totalCount: number;
  totalAmount: number;
  assignedCount: number;
  status: 'DRAFT' | 'PUBLISHED' | 'PROCESSING' | 'COMPLETED';
  publishTime?: string;
  createTime: string;
  updateTime: string;
}

export interface AssignmentRecord {
  id: number;
  casePackageId: number;
  sourceOrgId: number;
  disposalOrgId: number;
  caseCount: number;
  totalAmount: number;
  strategy: string;
  assignTime: string;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED';
}