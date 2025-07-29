package com.drmp.user.mapper;

import com.drmp.user.dto.OrganizationDTO;
import com.drmp.user.entity.Organization;
import org.mapstruct.*;

import java.util.List;

/**
 * 机构映射器
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrganizationMapper {
    
    /**
     * 实体转DTO
     */
    OrganizationDTO toDTO(Organization organization);
    
    /**
     * 实体列表转DTO列表
     */
    List<OrganizationDTO> toDTO(List<Organization> organizations);
    
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
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "auditStatus", ignore = true)
    @Mapping(target = "auditTime", ignore = true)
    @Mapping(target = "auditBy", ignore = true)
    Organization toEntity(OrganizationDTO organizationDTO);
    
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
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "auditStatus", ignore = true)
    @Mapping(target = "auditComment", ignore = true)
    @Mapping(target = "auditTime", ignore = true)
    @Mapping(target = "auditBy", ignore = true)
    void updateFromDTO(OrganizationDTO organizationDTO, @MappingTarget Organization organization);
}