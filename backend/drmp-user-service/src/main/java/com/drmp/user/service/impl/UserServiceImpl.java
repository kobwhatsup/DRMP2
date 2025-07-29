package com.drmp.user.service.impl;

import com.drmp.common.dto.PageResult;
import com.drmp.common.exception.BusinessException;
import com.drmp.user.dto.UserDTO;
import com.drmp.user.entity.Role;
import com.drmp.user.entity.User;
import com.drmp.user.mapper.UserMapper;
import com.drmp.user.repository.RoleRepository;
import com.drmp.user.repository.UserRepository;
import com.drmp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现类
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public PageResult<UserDTO> findByConditions(Long orgId, User.UserStatus status, String keyword, Pageable pageable) {
        log.debug("分页查询用户列表，机构ID：{}，状态：{}，关键词：{}", orgId, status, keyword);
        
        Page<User> page = userRepository.findByConditions(orgId, status, keyword, pageable);
        
        return PageResult.<UserDTO>builder()
                .records(userMapper.toDTO(page.getContent()))
                .total(page.getTotalElements())
                .current(page.getNumber() + 1)
                .size(page.getSize())
                .pages(page.getTotalPages())
                .build();
    }
    
    @Override
    public PageResult<UserDTO> findByOrgIdAndConditions(Long orgId, User.UserStatus status, String keyword, Pageable pageable) {
        log.debug("分页查询机构用户，机构ID：{}，状态：{}，关键词：{}", orgId, status, keyword);
        
        Page<User> page = userRepository.findByOrgIdAndConditions(orgId, status, keyword, pageable);
        
        return PageResult.<UserDTO>builder()
                .records(userMapper.toDTO(page.getContent()))
                .total(page.getTotalElements())
                .current(page.getNumber() + 1)
                .size(page.getSize())
                .pages(page.getTotalPages())
                .build();
    }
    
    @Override
    public UserDTO findById(Long id) {
        log.debug("查询用户详情，ID：{}", id);
        
        User user = userRepository.findById(id)
                .filter(u -> !u.getDeleted())
                .orElseThrow(() -> new BusinessException("用户不存在或已被删除"));
        
        return userMapper.toDTO(user);
    }
    
    @Override
    public UserDTO findByUsername(String username) {
        log.debug("根据用户名查询用户，用户名：{}", username);
        
        User user = userRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        
        return userMapper.toDTO(user);
    }
    
    @Override
    public UserDTO findByEmail(String email) {
        log.debug("根据邮箱查询用户，邮箱：{}", email);
        
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        
        return userMapper.toDTO(user);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO create(UserDTO userDTO) {
        log.info("创建用户，用户名：{}", userDTO.getUsername());
        
        // 验证用户名唯一性
        if (existsByUsername(userDTO.getUsername(), null)) {
            throw new BusinessException("用户名已存在");
        }
        
        // 验证邮箱唯一性
        if (StringUtils.hasText(userDTO.getEmail()) && existsByEmail(userDTO.getEmail(), null)) {
            throw new BusinessException("邮箱已存在");
        }
        
        // 转换DTO为实体
        User user = userMapper.toEntity(userDTO);
        
        // 加密密码
        if (StringUtils.hasText(userDTO.getPassword())) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        } else {
            // 设置默认密码
            user.setPassword(passwordEncoder.encode("123456"));
        }
        
        user.setPasswordUpdateTime(LocalDateTime.now());
        user.setStatus(User.UserStatus.ACTIVE);
        
        // 保存用户
        user = userRepository.save(user);
        
        // 分配默认角色
        assignDefaultRoles(user);
        
        log.info("用户创建成功，ID：{}，用户名：{}", user.getId(), user.getUsername());
        
        return userMapper.toDTO(user);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO update(Long id, UserDTO userDTO) {
        log.info("更新用户信息，ID：{}", id);
        
        User user = userRepository.findById(id)
                .filter(u -> !u.getDeleted())
                .orElseThrow(() -> new BusinessException("用户不存在或已被删除"));
        
        // 验证用户名唯一性
        if (!user.getUsername().equals(userDTO.getUsername()) &&
            existsByUsername(userDTO.getUsername(), id)) {
            throw new BusinessException("用户名已存在");
        }
        
        // 验证邮箱唯一性
        if (StringUtils.hasText(userDTO.getEmail()) &&
            !userDTO.getEmail().equals(user.getEmail()) &&
            existsByEmail(userDTO.getEmail(), id)) {
            throw new BusinessException("邮箱已存在");
        }
        
        // 更新用户信息（排除密码和状态）
        userMapper.updateFromDTO(userDTO, user);
        
        // 保存更新
        user = userRepository.save(user);
        
        log.info("用户信息更新成功，ID：{}，用户名：{}", user.getId(), user.getUsername());
        
        return userMapper.toDTO(user);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        log.info("删除用户，ID：{}", id);
        
        User user = userRepository.findById(id)
                .filter(u -> !u.getDeleted())
                .orElseThrow(() -> new BusinessException("用户不存在或已被删除"));
        
        // 逻辑删除
        user.setDeleted(true);
        userRepository.save(user);
        
        log.info("用户删除成功，ID：{}，用户名：{}", user.getId(), user.getUsername());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long id, String oldPassword, String newPassword) {
        log.info("修改用户密码，ID：{}", id);
        
        User user = userRepository.findById(id)
                .filter(u -> !u.getDeleted())
                .orElseThrow(() -> new BusinessException("用户不存在或已被删除"));
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码不正确");
        }
        
        // 更新密码
        userRepository.updatePassword(id, passwordEncoder.encode(newPassword), LocalDateTime.now());
        
        log.info("用户密码修改成功，ID：{}", id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long id, String newPassword) {
        log.info("重置用户密码，ID：{}", id);
        
        User user = userRepository.findById(id)
                .filter(u -> !u.getDeleted())
                .orElseThrow(() -> new BusinessException("用户不存在或已被删除"));
        
        // 更新密码
        userRepository.updatePassword(id, passwordEncoder.encode(newPassword), LocalDateTime.now());
        
        log.info("用户密码重置成功，ID：{}", id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, User.UserStatus status) {
        log.info("更新用户状态，ID：{}，状态：{}", id, status);
        
        User user = userRepository.findById(id)
                .filter(u -> !u.getDeleted())
                .orElseThrow(() -> new BusinessException("用户不存在或已被删除"));
        
        userRepository.updateStatus(id, status);
        
        log.info("用户状态更新成功，ID：{}，状态：{}", id, status);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        log.info("分配用户角色，用户ID：{}，角色IDs：{}", userId, roleIds);
        
        User user = userRepository.findById(userId)
                .filter(u -> !u.getDeleted())
                .orElseThrow(() -> new BusinessException("用户不存在或已被删除"));
        
        // 查询角色列表
        List<Role> roles = roleRepository.findAllById(roleIds);
        if (roles.size() != roleIds.size()) {
            throw new BusinessException("部分角色不存在");
        }
        
        // 更新用户角色
        user.setRoles(roles);
        userRepository.save(user);
        
        log.info("用户角色分配成功，用户ID：{}，角色数量：{}", userId, roles.size());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLastLoginInfo(Long userId, String loginIp) {
        log.debug("更新用户最后登录信息，用户ID：{}，IP：{}", userId, loginIp);
        
        userRepository.updateLastLoginInfo(userId, LocalDateTime.now(), loginIp);
    }
    
    @Override
    public boolean existsByUsername(String username, Long excludeId) {
        return userRepository.existsByUsernameAndIdNot(username, excludeId);
    }
    
    @Override
    public boolean existsByEmail(String email, Long excludeId) {
        return userRepository.existsByEmailAndIdNot(email, excludeId);
    }
    
    @Override
    public long countByOrgId(Long orgId) {
        return userRepository.countByOrgId(orgId);
    }
    
    @Override
    public long countActiveUsers() {
        return userRepository.countActiveUsers();
    }
    
    /**
     * 为新用户分配默认角色
     */
    private void assignDefaultRoles(User user) {
        // TODO: 根据机构类型分配默认角色
        // 这里可以根据业务逻辑来分配默认角色
    }
}