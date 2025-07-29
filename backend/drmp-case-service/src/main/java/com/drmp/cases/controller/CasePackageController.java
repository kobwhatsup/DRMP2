package com.drmp.cases.controller;

import com.drmp.cases.dto.CasePackageDTO;
import com.drmp.cases.dto.BatchImportResult;
import com.drmp.cases.entity.CasePackage;
import com.drmp.cases.service.CasePackageService;
import com.drmp.common.api.ApiResponse;
import com.drmp.common.api.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 案件包控制器
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/case-packages")
@RequiredArgsConstructor
@Validated
@Tag(name = "案件包管理", description = "案件包的增删改查、发布、导入等操作")
public class CasePackageController {
    
    private final CasePackageService casePackageService;
    
    @Operation(summary = "创建案件包", description = "创建新的案件包")
    @PostMapping
    @PreAuthorize("hasAuthority('CASE_PACKAGE_CREATE')")
    public ApiResponse<CasePackageDTO> createCasePackage(
            @Valid @RequestBody CasePackageDTO casePackageDTO) {
        log.info("创建案件包请求: {}", casePackageDTO.getName());
        
        CasePackageDTO result = casePackageService.createCasePackage(casePackageDTO);
        return ApiResponse.success(result);
    }
    
    @Operation(summary = "更新案件包", description = "更新案件包信息")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CASE_PACKAGE_UPDATE')")
    public ApiResponse<CasePackageDTO> updateCasePackage(
            @Parameter(description = "案件包ID") @PathVariable Long id,
            @Valid @RequestBody CasePackageDTO casePackageDTO) {
        log.info("更新案件包请求: ID={}", id);
        
        CasePackageDTO result = casePackageService.updateCasePackage(id, casePackageDTO);
        return ApiResponse.success(result);
    }
    
    @Operation(summary = "获取案件包详情", description = "根据ID获取案件包详细信息")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CASE_PACKAGE_READ')")
    public ApiResponse<CasePackageDTO> getCasePackage(
            @Parameter(description = "案件包ID") @PathVariable Long id) {
        
        CasePackageDTO result = casePackageService.getCasePackageById(id);
        return ApiResponse.success(result);
    }
    
    @Operation(summary = "删除案件包", description = "软删除案件包")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CASE_PACKAGE_DELETE')")
    public ApiResponse<Void> deleteCasePackage(
            @Parameter(description = "案件包ID") @PathVariable Long id) {
        log.info("删除案件包请求: ID={}", id);
        
        casePackageService.deleteCasePackage(id);
        return ApiResponse.success();
    }
    
    @Operation(summary = "分页查询案件包", description = "根据条件分页查询案件包列表")
    @GetMapping
    @PreAuthorize("hasAuthority('CASE_PACKAGE_READ')")
    public ApiResponse<PageResult<CasePackageDTO>> getCasePackages(
            @Parameter(description = "案源机构ID") @RequestParam(required = false) Long sourceOrgId,
            @Parameter(description = "状态") @RequestParam(required = false) CasePackage.CasePackageStatus status,
            @Parameter(description = "关键词搜索") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CasePackageDTO> result = casePackageService.getCasePackages(
            sourceOrgId, status, keyword, pageable);
        
        return ApiResponse.success(PageResult.of(result));
    }
    
    @Operation(summary = "获取已发布案件包", description = "分页查询已发布的案件包")
    @GetMapping("/published")
    @PreAuthorize("hasAuthority('CASE_PACKAGE_READ')")
    public ApiResponse<PageResult<CasePackageDTO>> getPublishedCasePackages(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CasePackageDTO> result = casePackageService.getPublishedCasePackages(pageable);
        
        return ApiResponse.success(PageResult.of(result));
    }
    
    @Operation(summary = "发布案件包", description = "将案件包状态更改为已发布")
    @PostMapping("/{id}/publish")
    @PreAuthorize("hasAuthority('CASE_PACKAGE_PUBLISH')")
    public ApiResponse<Void> publishCasePackage(
            @Parameter(description = "案件包ID") @PathVariable Long id) {
        log.info("发布案件包请求: ID={}", id);
        
        casePackageService.publishCasePackage(id);
        return ApiResponse.success();
    }
    
    @Operation(summary = "撤回案件包", description = "撤回已发布的案件包")
    @PostMapping("/{id}/withdraw")
    @PreAuthorize("hasAuthority('CASE_PACKAGE_WITHDRAW')")
    public ApiResponse<Void> withdrawCasePackage(
            @Parameter(description = "案件包ID") @PathVariable Long id) {
        log.info("撤回案件包请求: ID={}", id);
        
        casePackageService.withdrawCasePackage(id);
        return ApiResponse.success();
    }
    
    @Operation(summary = "关闭案件包", description = "关闭案件包，不再接受分案")
    @PostMapping("/{id}/close")
    @PreAuthorize("hasAuthority('CASE_PACKAGE_CLOSE')")
    public ApiResponse<Void> closeCasePackage(
            @Parameter(description = "案件包ID") @PathVariable Long id) {
        log.info("关闭案件包请求: ID={}", id);
        
        casePackageService.closeCasePackage(id);
        return ApiResponse.success();
    }
    
    @Operation(summary = "批量导入案件", description = "从Excel或CSV文件批量导入案件数据")
    @PostMapping("/{id}/import")
    @PreAuthorize("hasAuthority('CASE_IMPORT')")
    public ApiResponse<BatchImportResult> importCases(
            @Parameter(description = "案件包ID") @PathVariable Long id,
            @Parameter(description = "导入文件") @RequestParam("file") MultipartFile file) {
        log.info("批量导入案件请求: casePackageId={}, fileName={}", id, file.getOriginalFilename());
        
        // 验证文件
        validateImportFile(file);
        
        // 异步导入
        CompletableFuture<BatchImportResult> future = casePackageService.importCases(id, file);
        
        // 返回初始结果（包含taskId）
        try {
            BatchImportResult result = future.get();
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取导入结果失败", e);
            return ApiResponse.error("导入失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取导入进度", description = "根据任务ID获取导入进度")
    @GetMapping("/import/{taskId}/progress")
    @PreAuthorize("hasAuthority('CASE_IMPORT')")
    public ApiResponse<BatchImportResult> getImportProgress(
            @Parameter(description = "任务ID") @PathVariable String taskId) {
        
        BatchImportResult result = casePackageService.getImportProgress(taskId);
        if (result == null) {
            return ApiResponse.error("任务不存在或已过期");
        }
        
        return ApiResponse.success(result);
    }
    
    @Operation(summary = "更新案件包统计", description = "手动触发案件包统计信息更新")
    @PostMapping("/{id}/refresh-statistics")
    @PreAuthorize("hasAuthority('CASE_PACKAGE_UPDATE')")
    public ApiResponse<Void> refreshStatistics(
            @Parameter(description = "案件包ID") @PathVariable Long id) {
        log.info("更新案件包统计请求: ID={}", id);
        
        casePackageService.updateCasePackageStatistics(id);
        casePackageService.updateAssignmentStatistics(id);
        
        return ApiResponse.success();
    }
    
    @Operation(summary = "检查名称是否可用", description = "检查案件包名称在指定机构下是否可用")
    @GetMapping("/check-name")
    @PreAuthorize("hasAuthority('CASE_PACKAGE_READ')")
    public ApiResponse<Boolean> checkNameAvailable(
            @Parameter(description = "案源机构ID") @RequestParam @NotNull Long sourceOrgId,
            @Parameter(description = "案件包名称") @RequestParam @NotNull String name,
            @Parameter(description = "排除的案件包ID") @RequestParam(required = false) Long excludeId) {
        
        boolean exists = casePackageService.existsBySourceOrgIdAndName(sourceOrgId, name, excludeId);
        return ApiResponse.success(!exists);
    }
    
    @Operation(summary = "获取案件包状态统计", description = "获取各状态案件包的数量统计")
    @GetMapping("/statistics/status")
    @PreAuthorize("hasAuthority('CASE_PACKAGE_READ')")
    public ApiResponse<List<Object[]>> getStatusStatistics() {
        
        List<Object[]> result = casePackageService.getCasePackageStatusStatistics();
        return ApiResponse.success(result);
    }
    
    /**
     * 验证导入文件
     */
    private void validateImportFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("导入文件不能为空");
        }
        
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        
        // 检查文件格式
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        if (!"xlsx".equals(extension) && !"xls".equals(extension) && !"csv".equals(extension)) {
            throw new IllegalArgumentException("仅支持Excel(.xlsx/.xls)或CSV(.csv)格式文件");
        }
        
        // 检查文件大小（限制100MB）
        if (file.getSize() > 100 * 1024 * 1024) {
            throw new IllegalArgumentException("文件大小不能超过100MB");
        }
    }
}