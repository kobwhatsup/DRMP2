import { create } from 'zustand';

interface AppState {
  // 侧边栏折叠状态
  sidebarCollapsed: boolean;
  
  // 主题配置
  theme: 'light' | 'dark';
  
  // 语言配置
  locale: 'zh-CN' | 'en-US';
  
  // 页面加载状态
  loading: boolean;
  
  // Actions
  toggleSidebar: () => void;
  setSidebarCollapsed: (collapsed: boolean) => void;
  setTheme: (theme: 'light' | 'dark') => void;
  setLocale: (locale: 'zh-CN' | 'en-US') => void;
  setLoading: (loading: boolean) => void;
}

export const useAppStore = create<AppState>()((set) => ({
  sidebarCollapsed: false,
  theme: 'light',
  locale: 'zh-CN',
  loading: false,
  
  toggleSidebar: () => {
    set((state) => ({ sidebarCollapsed: !state.sidebarCollapsed }));
  },
  
  setSidebarCollapsed: (collapsed: boolean) => {
    set({ sidebarCollapsed: collapsed });
  },
  
  setTheme: (theme: 'light' | 'dark') => {
    set({ theme });
  },
  
  setLocale: (locale: 'zh-CN' | 'en-US') => {
    set({ locale });
  },
  
  setLoading: (loading: boolean) => {
    set({ loading });
  },
}));