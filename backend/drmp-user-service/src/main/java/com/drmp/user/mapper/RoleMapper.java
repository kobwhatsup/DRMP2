package com.drmp.user.mapper;

import com.drmp.user.dto.RoleDTO;
import com.drmp.user.entity.Role;
import org.mapstruct.*;

import java.util.List;

/**
 * 角色映射器
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {PermissionMapper.class})
public interface RoleMapper {
    
    /**
     * 实体转DTO
     */
    @Mapping(source = "permissions", target = "permissions")
    @Mapping(target = "permissionCodes", expression = "java(getPermissionCodes(role))")
    RoleDTO toDTO(Role role);
    
    /**
     * 实体列表转DTO列表
     */
    List<RoleDTO> toDTO(List<Role> roles);
    
    /**
     * DTO转实体
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    Role toEntity(RoleDTO roleDTO);
    
    /**
     * 更新实体（从DTO）
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    void updateFromDTO(RoleDTO roleDTO, @MappingTarget Role role);
    
    /**
     * 获取权限代码列表
     */
    default List<String> getPermissionCodes(Role role) {
        if (role.getPermissions() == null || role.getPermissions().isEmpty()) {
            return List.of();
        }
        
        return role.getPermissions().stream()
                .map(permission -> permission.getCode())
                .toList();
    }
}