package com.drmp.user.controller;

import com.drmp.common.dto.PageResult;
import com.drmp.common.dto.Result;
import com.drmp.common.enums.OrganizationType;
import com.drmp.user.dto.OrganizationDTO;
import com.drmp.user.entity.Organization;
import com.drmp.user.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 机构管理控制器
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
@Validated
@Tag(name = "机构管理", description = "机构注册、审核、查询等相关接口")
public class OrganizationController {
    
    private final OrganizationService organizationService;
    
    @GetMapping
    @Operation(summary = "分页查询机构列表", description = "根据条件分页查询机构信息")
    @PreAuthorize("hasPermission('ORG_LIST', 'READ')")
    public Result<PageResult<OrganizationDTO>> findByConditions(
            @Parameter(description = "机构类型") @RequestParam(required = false) OrganizationType type,
            @Parameter(description = "机构状态") @RequestParam(required = false) Organization.OrganizationStatus status,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(current - 1, size, 
                Sort.by(Sort.Direction.DESC, "createTime"));
        
        PageResult<OrganizationDTO> result = organizationService.findByConditions(
                type, status, keyword, pageable);
        
        return Result.success(result);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "查询机构详情", description = "根据ID查询机构详细信息")
    @PreAuthorize("hasPermission('ORG_VIEW', 'READ')")
    public Result<OrganizationDTO> findById(
            @Parameter(description = "机构ID", required = true) @PathVariable Long id) {
        
        OrganizationDTO organization = organizationService.findById(id);
        
        return Result.success(organization);
    }
    
    @PostMapping("/register")
    @Operation(summary = "机构注册", description = "新机构注册申请")
    public Result<OrganizationDTO> register(
            @Valid @RequestBody OrganizationDTO organizationDTO) {
        
        OrganizationDTO result = organizationService.create(organizationDTO);
        
        return Result.success("机构注册成功，请等待审核", result);
    }
    
    @PostMapping
    @Operation(summary = "创建机构", description = "管理员创建机构")
    @PreAuthorize("hasPermission('ORG_ADD', 'CREATE')")
    public Result<OrganizationDTO> create(
            @Valid @RequestBody OrganizationDTO organizationDTO) {
        
        OrganizationDTO result = organizationService.create(organizationDTO);
        
        return Result.success("机构创建成功", result);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新机构信息", description = "更新机构基本信息")
    @PreAuthorize("hasPermission('ORG_EDIT', 'UPDATE')")
    public Result<OrganizationDTO> update(
            @Parameter(description = "机构ID", required = true) @PathVariable Long id,
            @Valid @RequestBody OrganizationDTO organizationDTO) {
        
        OrganizationDTO result = organizationService.update(id, organizationDTO);
        
        return Result.success("机构信息更新成功", result);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除机构", description = "逻辑删除机构")
    @PreAuthorize("hasPermission('ORG_DELETE', 'DELETE')")
    public Result<Void> delete(
            @Parameter(description = "机构ID", required = true) @PathVariable Long id) {
        
        organizationService.delete(id);
        
        return Result.success("机构删除成功");
    }
    
    @PostMapping("/{id}/audit")
    @Operation(summary = "机构审核", description = "审核机构注册申请")
    @PreAuthorize("hasPermission('ORG_AUDIT', 'UPDATE')")
    public Result<Void> audit(
            @Parameter(description = "机构ID", required = true) @PathVariable Long id,
            @Parameter(description = "审核状态", required = true) @RequestParam Organization.AuditStatus auditStatus,
            @Parameter(description = "审核意见") @RequestParam(required = false) String auditComment) {
        
        return organizationService.audit(id, auditStatus, auditComment);
    }
    
    @PostMapping("/{id}/business-license")
    @Operation(summary = "上传营业执照", description = "上传机构营业执照文件")
    @PreAuthorize("hasPermission('ORG_EDIT', 'UPDATE')")
    public Result<String> uploadBusinessLicense(
            @Parameter(description = "机构ID", required = true) @PathVariable Long id,
            @Parameter(description = "营业执照文件", required = true) @RequestParam("file") MultipartFile file) {
        
        String filePath = organizationService.uploadBusinessLicense(id, file);
        
        return Result.success("营业执照上传成功", filePath);
    }
    
    @PostMapping("/{id}/contract")
    @Operation(summary = "上传合同文件", description = "上传合作合同文件")
    @PreAuthorize("hasPermission('ORG_EDIT', 'UPDATE')")
    public Result<String> uploadContractFile(
            @Parameter(description = "机构ID", required = true) @PathVariable Long id,
            @Parameter(description = "合同文件", required = true) @RequestParam("file") MultipartFile file) {
        
        String filePath = organizationService.uploadContractFile(id, file);
        
        return Result.success("合同文件上传成功", filePath);
    }
    
    @GetMapping("/disposal/active")
    @Operation(summary = "查询活跃处置机构", description = "获取所有活跃状态的处置机构列表")
    @PreAuthorize("hasPermission('ORG_LIST', 'READ')")
    public Result<List<OrganizationDTO>> findActiveDisposalOrganizations() {
        
        List<OrganizationDTO> organizations = organizationService.findActiveDisposalOrganizations();
        
        return Result.success(organizations);
    }
    
    @GetMapping("/disposal/by-region/{region}")
    @Operation(summary = "按区域查询处置机构", description = "根据服务区域查找处置机构")
    @PreAuthorize("hasPermission('ORG_LIST', 'READ')")
    public Result<List<OrganizationDTO>> findDisposalOrganizationsByRegion(
            @Parameter(description = "服务区域", required = true) @PathVariable String region) {
        
        List<OrganizationDTO> organizations = organizationService.findDisposalOrganizationsByRegion(region);
        
        return Result.success(organizations);
    }
    
    @GetMapping("/check-name")
    @Operation(summary = "检查机构名称", description = "检查机构名称是否已存在")
    public Result<Boolean> checkName(
            @Parameter(description = "机构名称", required = true) @RequestParam String name,
            @Parameter(description = "排除的机构ID") @RequestParam(required = false) Long excludeId) {
        
        boolean exists = organizationService.existsByName(name, excludeId);
        
        return Result.success(!exists);
    }
    
    @GetMapping("/check-credit-code")
    @Operation(summary = "检查统一社会信用代码", description = "检查统一社会信用代码是否已存在")
    public Result<Boolean> checkUnifiedCreditCode(
            @Parameter(description = "统一社会信用代码", required = true) @RequestParam String code,
            @Parameter(description = "排除的机构ID") @RequestParam(required = false) Long excludeId) {
        
        boolean exists = organizationService.existsByUnifiedCreditCode(code, excludeId);
        
        return Result.success(!exists);
    }
    
    @GetMapping("/stats/pending-audit")
    @Operation(summary = "待审核机构统计", description = "获取待审核机构数量")
    @PreAuthorize("hasPermission('ORG_AUDIT', 'READ')")
    public Result<Long> countPendingAudit() {
        
        long count = organizationService.countPendingAudit();
        
        return Result.success(count);
    }
}