package com.drmp.user.service;

import com.drmp.common.dto.PageResult;
import com.drmp.user.dto.UserDTO;
import com.drmp.user.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 用户服务接口
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
public interface UserService {
    
    /**
     * 分页查询用户列表
     *
     * @param orgId 机构ID（可选）
     * @param status 用户状态（可选）
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 分页结果
     */
    PageResult<UserDTO> findByConditions(Long orgId, User.UserStatus status, String keyword, Pageable pageable);
    
    /**
     * 分页查询机构下的用户
     *
     * @param orgId 机构ID
     * @param status 用户状态（可选）
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 分页结果
     */
    PageResult<UserDTO> findByOrgIdAndConditions(Long orgId, User.UserStatus status, String keyword, Pageable pageable);
    
    /**
     * 根据ID查询用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    UserDTO findById(Long id);
    
    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    UserDTO findByUsername(String username);
    
    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    UserDTO findByEmail(String email);
    
    /**
     * 创建用户
     *
     * @param userDTO 用户信息
     * @return 创建的用户
     */
    UserDTO create(UserDTO userDTO);
    
    /**
     * 更新用户信息
     *
     * @param id 用户ID
     * @param userDTO 用户信息
     * @return 更新后的用户
     */
    UserDTO update(Long id, UserDTO userDTO);
    
    /**
     * 删除用户（逻辑删除）
     *
     * @param id 用户ID
     */
    void delete(Long id);
    
    /**
     * 修改密码
     *
     * @param id 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(Long id, String oldPassword, String newPassword);
    
    /**
     * 重置密码
     *
     * @param id 用户ID
     * @param newPassword 新密码
     */
    void resetPassword(Long id, String newPassword);
    
    /**
     * 更新用户状态
     *
     * @param id 用户ID
     * @param status 新状态
     */
    void updateStatus(Long id, User.UserStatus status);
    
    /**
     * 分配角色
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    void assignRoles(Long userId, List<Long> roleIds);
    
    /**
     * 更新最后登录信息
     *
     * @param userId 用户ID
     * @param loginIp 登录IP
     */
    void updateLastLoginInfo(Long userId, String loginIp);
    
    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @param excludeId 排除的用户ID
     * @return 是否存在
     */
    boolean existsByUsername(String username, Long excludeId);
    
    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @param excludeId 排除的用户ID
     * @return 是否存在
     */
    boolean existsByEmail(String email, Long excludeId);
    
    /**
     * 获取机构下的用户数量
     *
     * @param orgId 机构ID
     * @return 用户数量
     */
    long countByOrgId(Long orgId);
    
    /**
     * 获取活跃用户数量
     *
     * @return 活跃用户数量
     */
    long countActiveUsers();
}