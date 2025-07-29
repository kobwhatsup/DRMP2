import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { UserInfo } from '@/types/api';

interface UserState {
  userInfo: UserInfo | null;
  accessToken: string | null;
  refreshToken: string | null;
  
  // Actions
  setUserInfo: (userInfo: UserInfo) => void;
  setTokens: (accessToken: string, refreshToken: string) => void;
  clearAuth: () => void;
  isAuthenticated: () => boolean;
  hasRole: (role: string) => boolean;
  hasPermission: (permission: string) => boolean;
}

export const useUserStore = create<UserState>()(
  persist(
    (set, get) => ({
      userInfo: null,
      accessToken: null,
      refreshToken: null,
      
      setUserInfo: (userInfo: UserInfo) => {
        set({ userInfo });
      },
      
      setTokens: (accessToken: string, refreshToken: string) => {
        set({ accessToken, refreshToken });
        // 同时存储到localStorage，供axios拦截器使用
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', refreshToken);
      },
      
      clearAuth: () => {
        set({
          userInfo: null,
          accessToken: null,
          refreshToken: null,
        });
        // 清除localStorage
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
      },
      
      isAuthenticated: () => {
        const { accessToken, userInfo } = get();
        return !!accessToken && !!userInfo;
      },
      
      hasRole: (role: string) => {
        const { userInfo } = get();
        return userInfo?.roles?.includes(role) || false;
      },
      
      hasPermission: (permission: string) => {
        const { userInfo } = get();
        return userInfo?.permissions?.includes(permission) || false;
      },
    }),
    {
      name: 'user-store',
      partialize: (state) => ({
        userInfo: state.userInfo,
        accessToken: state.accessToken,
        refreshToken: state.refreshToken,
      }),
    }
  )
);