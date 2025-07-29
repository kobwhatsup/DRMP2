import dayjs from 'dayjs';

/**
 * 格式化工具函数
 */

// 日期格式化
export const formatDate = (date: string | Date, format = 'YYYY-MM-DD') => {
  if (!date) return '';
  return dayjs(date).format(format);
};

export const formatDateTime = (date: string | Date) => {
  return formatDate(date, 'YYYY-MM-DD HH:mm:ss');
};

export const formatTime = (date: string | Date) => {
  return formatDate(date, 'HH:mm:ss');
};

// 金额格式化
export const formatMoney = (amount: number | string, precision = 2) => {
  if (amount === null || amount === undefined || amount === '') return '';
  
  const num = typeof amount === 'string' ? parseFloat(amount) : amount;
  if (isNaN(num)) return '';
  
  return new Intl.NumberFormat('zh-CN', {
    style: 'currency',
    currency: 'CNY',
    minimumFractionDigits: precision,
    maximumFractionDigits: precision,
  }).format(num);
};

// 数字格式化（千分位）
export const formatNumber = (num: number | string, precision = 0) => {
  if (num === null || num === undefined || num === '') return '';
  
  const value = typeof num === 'string' ? parseFloat(num) : num;
  if (isNaN(value)) return '';
  
  return new Intl.NumberFormat('zh-CN', {
    minimumFractionDigits: precision,
    maximumFractionDigits: precision,
  }).format(value);
};

// 百分比格式化
export const formatPercent = (num: number | string, precision = 2) => {
  if (num === null || num === undefined || num === '') return '';
  
  const value = typeof num === 'string' ? parseFloat(num) : num;
  if (isNaN(value)) return '';
  
  return `${(value * 100).toFixed(precision)}%`;
};

// 手机号格式化（脱敏）
export const formatPhone = (phone: string, mask = true) => {
  if (!phone) return '';
  
  if (mask && phone.length === 11) {
    return `${phone.slice(0, 3)}****${phone.slice(7)}`;
  }
  
  return phone;
};

// 身份证号格式化（脱敏）
export const formatIdCard = (idCard: string, mask = true) => {
  if (!idCard) return '';
  
  if (mask && idCard.length === 18) {
    return `${idCard.slice(0, 6)}********${idCard.slice(14)}`;
  }
  
  return idCard;
};

// 文件大小格式化
export const formatFileSize = (bytes: number) => {
  if (bytes === 0) return '0 B';
  
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  
  return `${parseFloat((bytes / Math.pow(k, i)).toFixed(2))} ${sizes[i]}`;
};

// 相对时间格式化
export const formatRelativeTime = (date: string | Date) => {
  if (!date) return '';
  
  const now = dayjs();
  const target = dayjs(date);
  const diff = now.diff(target, 'minute');
  
  if (diff < 1) {
    return '刚刚';
  } else if (diff < 60) {
    return `${diff}分钟前`;
  } else if (diff < 1440) {
    return `${Math.floor(diff / 60)}小时前`;
  } else if (diff < 43200) {
    return `${Math.floor(diff / 1440)}天前`;
  } else {
    return formatDate(date);
  }
};