package com.drmp.user.mapper;

import com.drmp.user.dto.PermissionDTO;
import com.drmp.user.entity.Permission;
import org.mapstruct.*;

import java.util.List;

/**
 * 权限映射器
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PermissionMapper {
    
    /**
     * 实体转DTO
     */
    PermissionDTO toDTO(Permission permission);
    
    /**
     * 实体列表转DTO列表
     */
    List<PermissionDTO> toDTO(List<Permission> permissions);
    
    /**
     * DTO转实体
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Permission toEntity(PermissionDTO permissionDTO);
    
    /**
     * 更新实体（从DTO）
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateFromDTO(PermissionDTO permissionDTO, @MappingTarget Permission permission);
}