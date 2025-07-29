package com.drmp.user.controller;

import com.drmp.common.dto.PageResult;
import com.drmp.common.dto.Result;
import com.drmp.user.dto.UserDTO;
import com.drmp.user.entity.User;
import com.drmp.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "用户管理", description = "用户增删改查、角色分配等相关接口")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping
    @Operation(summary = "分页查询用户列表", description = "根据条件分页查询用户信息")
    @PreAuthorize("hasPermission('USER_LIST', 'READ')")
    public Result<PageResult<UserDTO>> findByConditions(
            @Parameter(description = "机构ID") @RequestParam(required = false) Long orgId,
            @Parameter(description = "用户状态") @RequestParam(required = false) User.UserStatus status,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(current - 1, size, 
                Sort.by(Sort.Direction.DESC, "createTime"));
        
        PageResult<UserDTO> result = userService.findByConditions(orgId, status, keyword, pageable);
        
        return Result.success(result);
    }
    
    @GetMapping("/org/{orgId}")
    @Operation(summary = "查询机构用户", description = "分页查询指定机构下的用户")
    @PreAuthorize("hasPermission('USER_LIST', 'READ')")
    public Result<PageResult<UserDTO>> findByOrgId(
            @Parameter(description = "机构ID", required = true) @PathVariable Long orgId,
            @Parameter(description = "用户状态") @RequestParam(required = false) User.UserStatus status,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(current - 1, size, 
                Sort.by(Sort.Direction.DESC, "createTime"));
        
        PageResult<UserDTO> result = userService.findByOrgIdAndConditions(orgId, status, keyword, pageable);
        
        return Result.success(result);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "查询用户详情", description = "根据ID查询用户详细信息")
    @PreAuthorize("hasPermission('USER_VIEW', 'READ')")
    public Result<UserDTO> findById(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id) {
        
        UserDTO user = userService.findById(id);
        
        return Result.success(user);
    }
    
    @GetMapping("/username/{username}")
    @Operation(summary = "根据用户名查询用户", description = "根据用户名查询用户信息")
    @PreAuthorize("hasPermission('USER_VIEW', 'READ')")
    public Result<UserDTO> findByUsername(
            @Parameter(description = "用户名", required = true) @PathVariable String username) {
        
        UserDTO user = userService.findByUsername(username);
        
        return Result.success(user);
    }
    
    @PostMapping
    @Operation(summary = "创建用户", description = "创建新用户")
    @PreAuthorize("hasPermission('USER_ADD', 'CREATE')")
    public Result<UserDTO> create(
            @Valid @RequestBody UserDTO userDTO) {
        
        UserDTO result = userService.create(userDTO);
        
        return Result.success("用户创建成功", result);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新用户信息", description = "更新用户基本信息")
    @PreAuthorize("hasPermission('USER_EDIT', 'UPDATE')")
    public Result<UserDTO> update(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id,
            @Valid @RequestBody UserDTO userDTO) {
        
        UserDTO result = userService.update(id, userDTO);
        
        return Result.success("用户信息更新成功", result);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "逻辑删除用户")
    @PreAuthorize("hasPermission('USER_DELETE', 'DELETE')")
    public Result<Void> delete(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id) {
        
        userService.delete(id);
        
        return Result.success("用户删除成功");
    }
    
    @PostMapping("/{id}/change-password")
    @Operation(summary = "修改密码", description = "用户修改自己的密码")
    @PreAuthorize("hasPermission('USER_CHANGE_PASSWORD', 'UPDATE') or #id == authentication.principal.userId")
    public Result<Void> changePassword(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id,
            @Parameter(description = "旧密码", required = true) 
            @RequestParam @NotBlank @Size(min = 6, max = 50) String oldPassword,
            @Parameter(description = "新密码", required = true) 
            @RequestParam @NotBlank @Size(min = 6, max = 50) String newPassword) {
        
        userService.changePassword(id, oldPassword, newPassword);
        
        return Result.success("密码修改成功");
    }
    
    @PostMapping("/{id}/reset-password")
    @Operation(summary = "重置密码", description = "管理员重置用户密码")
    @PreAuthorize("hasPermission('USER_RESET_PASSWORD', 'UPDATE')")
    public Result<Void> resetPassword(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id,
            @Parameter(description = "新密码", required = true) 
            @RequestParam @NotBlank @Size(min = 6, max = 50) String newPassword) {
        
        userService.resetPassword(id, newPassword);
        
        return Result.success("密码重置成功");
    }
    
    @PostMapping("/{id}/status")
    @Operation(summary = "更新用户状态", description = "启用/禁用/锁定用户")
    @PreAuthorize("hasPermission('USER_EDIT', 'UPDATE')")
    public Result<Void> updateStatus(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id,
            @Parameter(description = "用户状态", required = true) @RequestParam User.UserStatus status) {
        
        userService.updateStatus(id, status);
        
        return Result.success("用户状态更新成功");
    }
    
    @PostMapping("/{id}/roles")
    @Operation(summary = "分配角色", description = "为用户分配角色")
    @PreAuthorize("hasPermission('USER_ASSIGN_ROLE', 'UPDATE')")
    public Result<Void> assignRoles(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id,
            @Parameter(description = "角色ID列表", required = true) @RequestBody List<Long> roleIds) {
        
        userService.assignRoles(id, roleIds);
        
        return Result.success("角色分配成功");
    }
    
    @GetMapping("/check-username")
    @Operation(summary = "检查用户名", description = "检查用户名是否已存在")
    public Result<Boolean> checkUsername(
            @Parameter(description = "用户名", required = true) @RequestParam String username,
            @Parameter(description = "排除的用户ID") @RequestParam(required = false) Long excludeId) {
        
        boolean exists = userService.existsByUsername(username, excludeId);
        
        return Result.success(!exists);
    }
    
    @GetMapping("/check-email")
    @Operation(summary = "检查邮箱", description = "检查邮箱是否已存在")
    public Result<Boolean> checkEmail(
            @Parameter(description = "邮箱", required = true) @RequestParam String email,
            @Parameter(description = "排除的用户ID") @RequestParam(required = false) Long excludeId) {
        
        boolean exists = userService.existsByEmail(email, excludeId);
        
        return Result.success(!exists);
    }
    
    @GetMapping("/stats/org/{orgId}/count")
    @Operation(summary = "机构用户统计", description = "获取机构下的用户数量")
    @PreAuthorize("hasPermission('USER_LIST', 'READ')")
    public Result<Long> countByOrgId(
            @Parameter(description = "机构ID", required = true) @PathVariable Long orgId) {
        
        long count = userService.countByOrgId(orgId);
        
        return Result.success(count);
    }
    
    @GetMapping("/stats/active-count")
    @Operation(summary = "活跃用户统计", description = "获取活跃用户数量")
    @PreAuthorize("hasPermission('USER_LIST', 'READ')")
    public Result<Long> countActiveUsers() {
        
        long count = userService.countActiveUsers();
        
        return Result.success(count);
    }
}