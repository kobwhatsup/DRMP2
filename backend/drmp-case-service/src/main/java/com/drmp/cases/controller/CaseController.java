package com.drmp.cases.controller;

import com.drmp.cases.dto.CaseDTO;
import com.drmp.cases.service.CaseService;
import com.drmp.common.api.ApiResponse;
import com.drmp.common.api.PageResult;
import com.drmp.common.enums.CaseStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 案件控制器
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/cases")
@RequiredArgsConstructor
@Validated
@Tag(name = "案件管理", description = "案件的增删改查、分案、状态更新等操作")
public class CaseController {
    
    private final CaseService caseService;
    
    @Operation(summary = "创建案件", description = "创建新的案件")
    @PostMapping
    @PreAuthorize("hasAuthority('CASE_CREATE')")
    public ApiResponse<CaseDTO> createCase(@Valid @RequestBody CaseDTO caseDTO) {
        log.info("创建案件请求: {}", caseDTO.getReceiptNumber());
        
        CaseDTO result = caseService.createCase(caseDTO);
        return ApiResponse.success(result);
    }
    
    @Operation(summary = "更新案件", description = "更新案件信息")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CASE_UPDATE')")
    public ApiResponse<CaseDTO> updateCase(
            @Parameter(description = "案件ID") @PathVariable Long id,
            @Valid @RequestBody CaseDTO caseDTO) {
        log.info("更新案件请求: ID={}", id);
        
        CaseDTO result = caseService.updateCase(id, caseDTO);
        return ApiResponse.success(result);
    }
    
    @Operation(summary = "获取案件详情", description = "根据ID获取案件详细信息")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CASE_READ')")
    public ApiResponse<CaseDTO> getCase(
            @Parameter(description = "案件ID") @PathVariable Long id) {
        
        CaseDTO result = caseService.getCaseById(id);
        return ApiResponse.success(result);
    }
    
    @Operation(summary = "删除案件", description = "软删除案件")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CASE_DELETE')")
    public ApiResponse<Void> deleteCase(
            @Parameter(description = "案件ID") @PathVariable Long id) {
        log.info("删除案件请求: ID={}", id);
        
        caseService.deleteCase(id);
        return ApiResponse.success();
    }
    
    @Operation(summary = "根据借据编号获取案件", description = "根据借据编号查询案件信息")
    @GetMapping("/receipt/{receiptNumber}")
    @PreAuthorize("hasAuthority('CASE_READ')")
    public ApiResponse<CaseDTO> getCaseByReceiptNumber(
            @Parameter(description = "借据编号") @PathVariable String receiptNumber) {
        
        CaseDTO result = caseService.getCaseByReceiptNumber(receiptNumber);
        return ApiResponse.success(result);
    }
    
    @Operation(summary = "分页查询案件", description = "根据条件分页查询案件列表")
    @GetMapping
    @PreAuthorize("hasAuthority('CASE_READ')")
    public ApiResponse<PageResult<CaseDTO>> getCases(
            @Parameter(description = "案件包ID") @RequestParam(required = false) Long casePackageId,
            @Parameter(description = "案件状态") @RequestParam(required = false) CaseStatus status,
            @Parameter(description = "分配机构ID") @RequestParam(required = false) Long assignedOrgId,
            @Parameter(description = "关键词搜索") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CaseDTO> result = caseService.getCases(
            casePackageId, status, assignedOrgId, keyword, pageable);
        
        return ApiResponse.success(PageResult.of(result));
    }
    
    @Operation(summary = "根据案件包查询案件", description = "分页查询指定案件包下的案件")
    @GetMapping("/package/{casePackageId}")
    @PreAuthorize("hasAuthority('CASE_READ')")
    public ApiResponse<PageResult<CaseDTO>> getCasesByCasePackage(
            @Parameter(description = "案件包ID") @PathVariable Long casePackageId,
            @Parameter(description = "案件状态") @RequestParam(required = false) CaseStatus status,
            @Parameter(description = "关键词搜索") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CaseDTO> result = caseService.getCasesByCasePackageId(
            casePackageId, status, keyword, pageable);
        
        return ApiResponse.success(PageResult.of(result));
    }
    
    @Operation(summary = "获取处置机构案件", description = "分页查询分配给指定处置机构的案件")
    @GetMapping("/organization/{orgId}")
    @PreAuthorize("hasAuthority('CASE_READ')")
    public ApiResponse<PageResult<CaseDTO>> getCasesByOrganization(
            @Parameter(description = "处置机构ID") @PathVariable Long orgId,
            @Parameter(description = "案件状态") @RequestParam(required = false) CaseStatus status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CaseDTO> result = caseService.getCasesByAssignedOrg(orgId, status, pageable);
        
        return ApiResponse.success(PageResult.of(result));
    }
    
    @Operation(summary = "批量分配案件", description = "将多个案件分配给指定的处置机构")
    @PostMapping("/assign")
    @PreAuthorize("hasAuthority('CASE_ASSIGN')")
    public ApiResponse<Void> assignCases(
            @Parameter(description = "案件ID列表") @RequestBody @NotEmpty List<Long> caseIds,
            @Parameter(description = "处置机构ID") @RequestParam @NotNull Long orgId) {
        log.info("批量分配案件请求: caseIds={}, orgId={}", caseIds, orgId);
        
        caseService.assignCases(caseIds, orgId);
        return ApiResponse.success();
    }
    
    @Operation(summary = "更新案件状态", description = "更新案件的处理状态和进展")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('CASE_UPDATE')")
    public ApiResponse<Void> updateCaseStatus(
            @Parameter(description = "案件ID") @PathVariable Long id,
            @Parameter(description = "新状态") @RequestParam @NotNull CaseStatus status,
            @Parameter(description = "处理进展") @RequestParam(required = false) String progress) {
        log.info("更新案件状态请求: ID={}, status={}", id, status);
        
        caseService.updateCaseStatus(id, status, progress);
        return ApiResponse.success();
    }
    
    @Operation(summary = "更新回款信息", description = "更新案件的回款金额和回款率")
    @PutMapping("/{id}/recovery")
    @PreAuthorize("hasAuthority('CASE_UPDATE')")
    public ApiResponse<Void> updateRecoveryInfo(
            @Parameter(description = "案件ID") @PathVariable Long id,
            @Parameter(description = "总回款金额") @RequestParam @NotNull BigDecimal totalRecovered,
            @Parameter(description = "回款率") @RequestParam @NotNull BigDecimal recoveryRate) {
        log.info("更新回款信息请求: ID={}, totalRecovered={}, recoveryRate={}", 
                id, totalRecovered, recoveryRate);
        
        caseService.updateRecoveryInfo(id, totalRecovered, recoveryRate);
        return ApiResponse.success();
    }
    
    @Operation(summary = "检查借据编号", description = "检查借据编号是否已存在")
    @GetMapping("/check-receipt-number")
    @PreAuthorize("hasAuthority('CASE_READ')")
    public ApiResponse<Boolean> checkReceiptNumber(
            @Parameter(description = "借据编号") @RequestParam @NotNull String receiptNumber,
            @Parameter(description = "排除的案件ID") @RequestParam(required = false) Long excludeId) {
        
        boolean exists = caseService.existsByReceiptNumber(receiptNumber, excludeId);
        return ApiResponse.success(!exists);
    }
    
    @Operation(summary = "获取待分案案件", description = "查询所有待分配的案件")
    @GetMapping("/pending-assignment")
    @PreAuthorize("hasAuthority('CASE_READ')")
    public ApiResponse<List<CaseDTO>> getPendingAssignmentCases() {
        
        List<CaseDTO> result = caseService.getPendingAssignmentCases();
        return ApiResponse.success(result);
    }
    
    @Operation(summary = "获取超期案件", description = "查询超过指定天数未处理的案件")
    @GetMapping("/overdue")
    @PreAuthorize("hasAuthority('CASE_READ')")
    public ApiResponse<List<CaseDTO>> getOverdueCases(
            @Parameter(description = "超期天数") @RequestParam(defaultValue = "7") int timeoutDays) {
        
        List<CaseDTO> result = caseService.getOverdueCases(timeoutDays);
        return ApiResponse.success(result);
    }
    
    @Operation(summary = "根据逾期天数查询案件", description = "查询指定逾期天数范围内的案件")
    @GetMapping("/by-overdue-days")
    @PreAuthorize("hasAuthority('CASE_READ')")
    public ApiResponse<List<CaseDTO>> getCasesByOverdueDays(
            @Parameter(description = "最小逾期天数") @RequestParam @NotNull Integer minDays,
            @Parameter(description = "最大逾期天数") @RequestParam @NotNull Integer maxDays) {
        
        List<CaseDTO> result = caseService.getCasesByOverdueDaysRange(minDays, maxDays);
        return ApiResponse.success(result);
    }
    
    @Operation(summary = "获取案件状态统计", description = "统计各状态案件的数量分布")
    @GetMapping("/statistics/status")
    @PreAuthorize("hasAuthority('CASE_READ')")
    public ApiResponse<Map<String, Object>> getCaseStatusStatistics() {
        
        List<Object[]> rawData = caseService.getCaseStatusStatistics();
        
        // 转换为更友好的格式
        Map<String, Object> result = new java.util.HashMap<>();
        for (Object[] row : rawData) {
            CaseStatus status = (CaseStatus) row[0];
            Long count = (Long) row[1];
            result.put(status.name(), count);
        }
        
        return ApiResponse.success(result);
    }
    
    @Operation(summary = "获取机构案件统计", description = "统计指定处置机构各状态案件数量")
    @GetMapping("/statistics/organization/{orgId}")
    @PreAuthorize("hasAuthority('CASE_READ')")
    public ApiResponse<Map<String, Object>> getOrgCaseStatistics(
            @Parameter(description = "处置机构ID") @PathVariable Long orgId) {
        
        List<Object[]> rawData = caseService.getOrgCaseStatistics(orgId);
        
        // 转换为更友好的格式
        Map<String, Object> result = new java.util.HashMap<>();
        for (Object[] row : rawData) {
            CaseStatus status = (CaseStatus) row[0];
            Long count = (Long) row[1];
            result.put(status.name(), count);
        }
        
        return ApiResponse.success(result);
    }
    
    @Operation(summary = "获取回款统计", description = "获取指定处置机构的回款统计信息")
    @GetMapping("/statistics/recovery/{orgId}")
    @PreAuthorize("hasAuthority('CASE_READ')")
    public ApiResponse<Map<String, Object>> getRecoveryStatistics(
            @Parameter(description = "处置机构ID") @PathVariable Long orgId) {
        
        Object[] rawData = caseService.getRecoveryStatistics(orgId);
        
        Map<String, Object> result = new java.util.HashMap<>();
        if (rawData != null && rawData.length >= 3) {
            result.put("caseCount", rawData[0]);           // 有回款的案件数
            result.put("totalRecovered", rawData[1]);      // 总回款金额
            result.put("avgRecoveryRate", rawData[2]);     // 平均回款率
        } else {
            result.put("caseCount", 0);
            result.put("totalRecovered", BigDecimal.ZERO);
            result.put("avgRecoveryRate", BigDecimal.ZERO);
        }
        
        return ApiResponse.success(result);
    }
    
    @Operation(summary = "计算逾期等级", description = "根据逾期天数计算逾期等级")
    @GetMapping("/calculate-overdue-level")
    public ApiResponse<String> calculateOverdueLevel(
            @Parameter(description = "逾期天数") @RequestParam @NotNull Integer overdueDays) {
        
        String level = caseService.calculateOverdueLevel(overdueDays);
        return ApiResponse.success(level);
    }
    
    @Operation(summary = "计算风险等级", description = "根据案件信息计算风险等级")
    @PostMapping("/calculate-risk-level")
    public ApiResponse<String> calculateRiskLevel(@RequestBody CaseDTO caseDTO) {
        
        String level = caseService.calculateRiskLevel(caseDTO);
        return ApiResponse.success(level);
    }
}