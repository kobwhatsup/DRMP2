package com.drmp.user.mapper;

import com.drmp.user.dto.UserDTO;
import com.drmp.user.entity.User;
import org.mapstruct.*;

import java.util.List;

/**
 * 用户映射器
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {RoleMapper.class})
public interface UserMapper {
    
    /**
     * 实体转DTO
     */
    @Mapping(source = "organization.name", target = "orgName")
    @Mapping(source = "organization.type", target = "orgType")
    @Mapping(source = "roles", target = "roles")
    @Mapping(target = "permissions", expression = "java(getPermissions(user))")
    UserDTO toDTO(User user);
    
    /**
     * 实体列表转DTO列表
     */
    List<UserDTO> toDTO(List<User> users);
    
    /**
     * DTO转实体
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "lastLoginTime", ignore = true)
    @Mapping(target = "lastLoginIp", ignore = true)
    @Mapping(target = "loginCount", ignore = true)
    @Mapping(target = "passwordUpdateTime", ignore = true)
    @Mapping(target = "mfaSecret", ignore = true)
    User toEntity(UserDTO userDTO);
    
    /**
     * 更新实体（从DTO）
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "lastLoginTime", ignore = true)
    @Mapping(target = "lastLoginIp", ignore = true)
    @Mapping(target = "loginCount", ignore = true)
    @Mapping(target = "passwordUpdateTime", ignore = true)
    @Mapping(target = "mfaEnabled", ignore = true)
    @Mapping(target = "mfaSecret", ignore = true)
    void updateFromDTO(UserDTO userDTO, @MappingTarget User user);
    
    /**
     * 获取用户权限列表
     */
    default List<String> getPermissions(User user) {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return List.of();
        }
        
        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> permission.getCode())
                .distinct()
                .toList();
    }
}