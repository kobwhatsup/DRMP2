package com.drmp.user.dto;

import com.drmp.user.entity.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色数据传输对象
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Data
@Schema(description = "角色信息")
public class RoleDTO {
    
    @Schema(description = "角色ID")
    private Long id;
    
    @Schema(description = "角色名称", required = true)
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称长度不能超过50字符")
    private String name;
    
    @Schema(description = "角色编码", required = true)
    @NotBlank(message = "角色编码不能为空")
    @Size(max = 50, message = "角色编码长度不能超过50字符")
    private String code;
    
    @Schema(description = "角色描述")
    @Size(max = 255, message = "角色描述长度不能超过255字符")
    private String description;
    
    @Schema(description = "适用机构类型")
    private Role.OrgType orgType;
    
    @Schema(description = "是否默认角色")
    private Boolean isDefault;
    
    @Schema(description = "排序序号")
    private Integer sortOrder;
    
    @Schema(description = "权限ID列表")
    private List<Long> permissionIds;
    
    @Schema(description = "权限列表")
    private List<PermissionDTO> permissions;
    
    @Schema(description = "权限代码列表")
    private List<String> permissionCodes;
    
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}