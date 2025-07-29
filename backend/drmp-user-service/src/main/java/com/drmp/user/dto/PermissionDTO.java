package com.drmp.user.dto;

import com.drmp.user.entity.Permission;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限数据传输对象
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Data
@Schema(description = "权限信息")
public class PermissionDTO {
    
    @Schema(description = "权限ID")
    private Long id;
    
    @Schema(description = "权限名称", required = true)
    @NotBlank(message = "权限名称不能为空")
    @Size(max = 100, message = "权限名称长度不能超过100字符")
    private String name;
    
    @Schema(description = "权限编码", required = true)
    @NotBlank(message = "权限编码不能为空")
    @Size(max = 100, message = "权限编码长度不能超过100字符")
    private String code;
    
    @Schema(description = "权限类型", required = true, allowableValues = {"MENU", "BUTTON", "API"})
    @NotNull(message = "权限类型不能为空")
    private Permission.PermissionType type;
    
    @Schema(description = "父权限ID")
    private Long parentId;
    
    @Schema(description = "菜单路径或API路径")
    @Size(max = 255, message = "路径长度不能超过255字符")
    private String path;
    
    @Schema(description = "HTTP方法")
    @Size(max = 10, message = "HTTP方法长度不能超过10字符")
    private String method;
    
    @Schema(description = "图标")
    @Size(max = 100, message = "图标长度不能超过100字符")
    private String icon;
    
    @Schema(description = "排序序号")
    private Integer sortOrder;
    
    @Schema(description = "权限描述")
    @Size(max = 255, message = "权限描述长度不能超过255字符")
    private String description;
    
    @Schema(description = "子权限列表")
    private List<PermissionDTO> children;
    
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}