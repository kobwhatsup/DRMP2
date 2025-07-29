package com.drmp.cases.service.impl;

import com.drmp.cases.dto.CasePackageDTO;
import com.drmp.cases.dto.BatchImportResult;
import com.drmp.cases.dto.CaseImportDTO;
import com.drmp.cases.entity.CasePackage;
import com.drmp.cases.repository.CasePackageRepository;
import com.drmp.cases.repository.CaseRepository;
import com.drmp.cases.service.CasePackageService;
import com.drmp.cases.service.CaseService;
import com.drmp.common.exception.BusinessException;
import com.drmp.common.exception.ErrorCode;
import com.drmp.common.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 案件包服务实现类
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CasePackageServiceImpl implements CasePackageService {
    
    private final CasePackageRepository casePackageRepository;
    private final CaseRepository caseRepository;
    private final CaseService caseService;
    
    // 导入任务缓存
    private final Map<String, BatchImportResult> importTaskCache = new ConcurrentHashMap<>();
    
    @Override
    @Transactional
    public CasePackageDTO createCasePackage(CasePackageDTO casePackageDTO) {
        log.info("创建案件包: {}", casePackageDTO.getName());
        
        // 验证数据
        validateCasePackage(casePackageDTO);
        
        // 检查名称是否重复
        if (existsBySourceOrgIdAndName(casePackageDTO.getSourceOrgId(), 
                                     casePackageDTO.getName(), null)) {
            throw new BusinessException(ErrorCode.CASE_PACKAGE_NAME_EXISTS);
        }
        
        // 创建实体
        CasePackage casePackage = new CasePackage();
        casePackage.setSourceOrgId(casePackageDTO.getSourceOrgId());
        casePackage.setName(casePackageDTO.getName());
        casePackage.setDescription(casePackageDTO.getDescription());
        casePackage.setExpectedRecoveryRate(casePackageDTO.getExpectedRecoveryRate());
        casePackage.setExpectedPeriod(casePackageDTO.getExpectedPeriod());
        casePackage.setPreferredMethods(JsonUtils.toJsonString(casePackageDTO.getPreferredMethods()));
        casePackage.setAssignmentStrategy(JsonUtils.toJsonString(casePackageDTO.getAssignmentStrategy()));
        casePackage.setStatus(CasePackage.CasePackageStatus.DRAFT);
        casePackage.setImportStatus(CasePackage.ImportStatus.PENDING);
        casePackage.setTotalCount(0);
        casePackage.setTotalAmount(BigDecimal.ZERO);
        casePackage.setAssignedCount(0);
        casePackage.setAssignedAmount(BigDecimal.ZERO);
        casePackage.setImportProgress(0);
        
        casePackage = casePackageRepository.save(casePackage);
        
        log.info("案件包创建成功, ID: {}", casePackage.getId());
        return convertToDTO(casePackage);
    }
    
    @Override
    @Transactional
    public CasePackageDTO updateCasePackage(Long id, CasePackageDTO casePackageDTO) {
        log.info("更新案件包: ID={}", id);
        
        CasePackage casePackage = casePackageRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.CASE_PACKAGE_NOT_FOUND));
        
        // 检查状态是否允许修改
        if (casePackage.getStatus() == CasePackage.CasePackageStatus.PUBLISHED ||
            casePackage.getStatus() == CasePackage.CasePackageStatus.PROCESSING) {
            throw new BusinessException(ErrorCode.CASE_PACKAGE_CANNOT_MODIFY);
        }
        
        // 验证数据
        validateCasePackage(casePackageDTO);
        
        // 检查名称是否重复
        if (existsBySourceOrgIdAndName(casePackageDTO.getSourceOrgId(), 
                                     casePackageDTO.getName(), id)) {
            throw new BusinessException(ErrorCode.CASE_PACKAGE_NAME_EXISTS);
        }
        
        // 更新字段
        casePackage.setName(casePackageDTO.getName());
        casePackage.setDescription(casePackageDTO.getDescription());
        casePackage.setExpectedRecoveryRate(casePackageDTO.getExpectedRecoveryRate());
        casePackage.setExpectedPeriod(casePackageDTO.getExpectedPeriod());
        casePackage.setPreferredMethods(JsonUtils.toJsonString(casePackageDTO.getPreferredMethods()));
        casePackage.setAssignmentStrategy(JsonUtils.toJsonString(casePackageDTO.getAssignmentStrategy()));
        
        casePackage = casePackageRepository.save(casePackage);
        
        log.info("案件包更新成功, ID: {}", id);
        return convertToDTO(casePackage);
    }
    
    @Override
    public CasePackageDTO getCasePackageById(Long id) {
        CasePackage casePackage = casePackageRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.CASE_PACKAGE_NOT_FOUND));
        return convertToDTO(casePackage);
    }
    
    @Override
    @Transactional
    public void deleteCasePackage(Long id) {
        log.info("删除案件包: ID={}", id);
        
        CasePackage casePackage = casePackageRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.CASE_PACKAGE_NOT_FOUND));
        
        // 检查状态是否允许删除
        if (casePackage.getStatus() == CasePackage.CasePackageStatus.PUBLISHED ||
            casePackage.getStatus() == CasePackage.CasePackageStatus.PROCESSING) {
            throw new BusinessException(ErrorCode.CASE_PACKAGE_CANNOT_DELETE);
        }
        
        // 软删除
        casePackage.setDeleted(true);
        casePackageRepository.save(casePackage);
        
        log.info("案件包删除成功, ID: {}", id);
    }
    
    @Override
    public Page<CasePackageDTO> getCasePackages(Long sourceOrgId, 
                                              CasePackage.CasePackageStatus status,
                                              String keyword,
                                              Pageable pageable) {
        Page<CasePackage> casePackages = casePackageRepository.findByConditions(
            sourceOrgId, status, keyword, pageable);
        return casePackages.map(this::convertToDTO);
    }
    
    @Override
    public Page<CasePackageDTO> getPublishedCasePackages(Pageable pageable) {
        Page<CasePackage> casePackages = casePackageRepository.findPublishedCasePackages(pageable);
        return casePackages.map(this::convertToDTO);
    }
    
    @Override
    @Transactional
    public void publishCasePackage(Long id) {
        log.info("发布案件包: ID={}", id);
        
        CasePackage casePackage = casePackageRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.CASE_PACKAGE_NOT_FOUND));
        
        // 检查状态
        if (casePackage.getStatus() != CasePackage.CasePackageStatus.DRAFT &&
            casePackage.getStatus() != CasePackage.CasePackageStatus.WITHDRAWN) {
            throw new BusinessException(ErrorCode.CASE_PACKAGE_CANNOT_PUBLISH);
        }
        
        // 检查是否有案件数据
        if (casePackage.getTotalCount() == null || casePackage.getTotalCount() <= 0) {
            throw new BusinessException(ErrorCode.CASE_PACKAGE_NO_CASES);
        }
        
        // 发布案件包
        casePackageRepository.publishCasePackage(id, 
            CasePackage.CasePackageStatus.PUBLISHED, LocalDateTime.now());
        
        log.info("案件包发布成功, ID: {}", id);
    }
    
    @Override
    @Transactional
    public void withdrawCasePackage(Long id) {
        log.info("撤回案件包: ID={}", id);
        
        CasePackage casePackage = casePackageRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.CASE_PACKAGE_NOT_FOUND));
        
        // 检查状态
        if (casePackage.getStatus() != CasePackage.CasePackageStatus.PUBLISHED) {
            throw new BusinessException(ErrorCode.CASE_PACKAGE_CANNOT_WITHDRAW);
        }
        
        // 撤回案件包
        casePackage.setStatus(CasePackage.CasePackageStatus.WITHDRAWN);
        casePackageRepository.save(casePackage);
        
        log.info("案件包撤回成功, ID: {}", id);
    }
    
    @Override
    @Transactional
    public void closeCasePackage(Long id) {
        log.info("关闭案件包: ID={}", id);
        
        CasePackage casePackage = casePackageRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.CASE_PACKAGE_NOT_FOUND));
        
        // 关闭案件包
        casePackage.setStatus(CasePackage.CasePackageStatus.CLOSED);
        casePackageRepository.save(casePackage);
        
        log.info("案件包关闭成功, ID: {}", id);
    }
    
    @Override
    @Async
    public CompletableFuture<BatchImportResult> importCases(Long casePackageId, MultipartFile file) {
        String taskId = UUID.randomUUID().toString();
        log.info("开始批量导入案件: casePackageId={}, taskId={}, fileName={}", 
                casePackageId, taskId, file.getOriginalFilename());
        
        // 初始化导入结果
        BatchImportResult result = new BatchImportResult();
        result.setTaskId(taskId);
        result.setFileName(file.getOriginalFilename());
        result.setStatus(BatchImportResult.ImportStatus.PROCESSING);
        result.setProgress(0);
        result.setStartTime(LocalDateTime.now());
        importTaskCache.put(taskId, result);
        
        try {
            // 检查案件包是否存在
            CasePackage casePackage = casePackageRepository.findById(casePackageId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CASE_PACKAGE_NOT_FOUND));
            
            // 更新导入状态
            casePackageRepository.updateImportStatus(casePackageId, 
                CasePackage.ImportStatus.PROCESSING, 0, null);
            
            // 解析文件
            String filePath = saveUploadedFile(file);
            List<CaseImportDTO> importData = caseService.parseImportFile(filePath, file.getOriginalFilename());
            result.setTotalRecords(importData.size());
            result.setProgress(20);
            importTaskCache.put(taskId, result);
            
            // 验证数据
            List<CaseImportDTO> validatedData = caseService.validateImportData(importData);
            result.setProgress(40);
            importTaskCache.put(taskId, result);
            
            // 导入数据
            caseService.batchImportCases(casePackageId, validatedData);
            result.setProgress(80);
            importTaskCache.put(taskId, result);
            
            // 更新统计信息
            updateCasePackageStatistics(casePackageId);
            result.setProgress(100);
            
            // 计算结果
            int successCount = (int) validatedData.stream().filter(d -> d.getValid()).count();
            int failureCount = validatedData.size() - successCount;
            
            result.setSuccessCount(successCount);
            result.setFailureCount(failureCount);
            result.setSkipCount(0);
            result.setEndTime(LocalDateTime.now());
            result.setDuration(java.time.Duration.between(result.getStartTime(), result.getEndTime()).getSeconds());
            
            if (failureCount == 0) {
                result.setStatus(BatchImportResult.ImportStatus.SUCCESS);
                casePackageRepository.updateImportStatus(casePackageId, 
                    CasePackage.ImportStatus.SUCCESS, 100, null);
            } else if (successCount > 0) {
                result.setStatus(BatchImportResult.ImportStatus.PARTIAL_SUCCESS);
                casePackageRepository.updateImportStatus(casePackageId, 
                    CasePackage.ImportStatus.PARTIAL_SUCCESS, 100, 
                    String.format("成功导入%d条，失败%d条", successCount, failureCount));
            } else {
                result.setStatus(BatchImportResult.ImportStatus.FAILED);
                casePackageRepository.updateImportStatus(casePackageId, 
                    CasePackage.ImportStatus.FAILED, 100, "所有数据导入失败");
            }
            
            importTaskCache.put(taskId, result);
            log.info("批量导入案件完成: taskId={}, success={}, failure={}", 
                    taskId, successCount, failureCount);
            
        } catch (Exception e) {
            log.error("批量导入案件失败: taskId={}", taskId, e);
            
            result.setStatus(BatchImportResult.ImportStatus.FAILED);
            result.setErrorMessage(e.getMessage());
            result.setEndTime(LocalDateTime.now());
            result.setProgress(0);
            importTaskCache.put(taskId, result);
            
            casePackageRepository.updateImportStatus(casePackageId, 
                CasePackage.ImportStatus.FAILED, 0, e.getMessage());
        }
        
        return CompletableFuture.completedFuture(result);
    }
    
    @Override
    public BatchImportResult getImportProgress(String taskId) {
        return importTaskCache.get(taskId);
    }
    
    @Override
    @Transactional
    public void updateCasePackageStatistics(Long id) {
        Object[] stats = caseRepository.countAndSumByCasePackageId(id);
        if (stats != null && stats.length >= 2) {
            Long count = (Long) stats[0];
            BigDecimal amount = (BigDecimal) stats[1];
            casePackageRepository.updateCasePackageStatistics(id, count.intValue(), amount);
        }
    }
    
    @Override
    @Transactional
    public void updateAssignmentStatistics(Long id) {
        Object[] stats = caseRepository.countAndSumAssignedByCasePackageId(id);
        if (stats != null && stats.length >= 2) {
            Long count = (Long) stats[0];
            BigDecimal amount = (BigDecimal) stats[1];
            casePackageRepository.updateAssignmentStatistics(id, count.intValue(), amount);
        }
    }
    
    @Override
    public boolean existsBySourceOrgIdAndName(Long sourceOrgId, String name, Long excludeId) {
        return casePackageRepository.existsBySourceOrgIdAndNameAndIdNot(sourceOrgId, name, excludeId);
    }
    
    @Override
    public List<Object[]> getOrgCasePackageStatistics(Long sourceOrgId) {
        // 可以根据需要实现更复杂的统计逻辑
        return List.of();
    }
    
    @Override
    public List<Object[]> getCasePackageStatusStatistics() {
        return casePackageRepository.countByStatus();
    }
    
    @Override
    @Transactional
    public void processTimeoutImports() {
        LocalDateTime timeoutTime = LocalDateTime.now().minusHours(2); // 2小时超时
        List<CasePackage> timeoutImports = casePackageRepository.findTimeoutImports(timeoutTime);
        
        for (CasePackage casePackage : timeoutImports) {
            log.warn("导入任务超时: casePackageId={}", casePackage.getId());
            casePackageRepository.updateImportStatus(casePackage.getId(), 
                CasePackage.ImportStatus.FAILED, 0, "导入任务超时");
        }
    }
    
    @Override
    public void validateCasePackage(CasePackageDTO casePackageDTO) {
        if (casePackageDTO.getSourceOrgId() == null) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "案源机构ID不能为空");
        }
        
        if (casePackageDTO.getName() == null || casePackageDTO.getName().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "案件包名称不能为空");
        }
        
        if (casePackageDTO.getName().length() > 200) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "案件包名称长度不能超过200字符");
        }
        
        if (casePackageDTO.getExpectedRecoveryRate() != null) {
            if (casePackageDTO.getExpectedRecoveryRate().compareTo(BigDecimal.ZERO) < 0 ||
                casePackageDTO.getExpectedRecoveryRate().compareTo(new BigDecimal("100")) > 0) {
                throw new BusinessException(ErrorCode.INVALID_PARAMETER, "期望回款率必须在0-100之间");
            }
        }
        
        if (casePackageDTO.getExpectedPeriod() != null && casePackageDTO.getExpectedPeriod() <= 0) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "期望处置周期必须大于0");
        }
    }
    
    /**
     * 实体转DTO
     */
    private CasePackageDTO convertToDTO(CasePackage casePackage) {
        CasePackageDTO dto = new CasePackageDTO();
        dto.setId(casePackage.getId());
        dto.setSourceOrgId(casePackage.getSourceOrgId());
        dto.setName(casePackage.getName());
        dto.setDescription(casePackage.getDescription());
        dto.setTotalCount(casePackage.getTotalCount());
        dto.setTotalAmount(casePackage.getTotalAmount());
        dto.setAssignedCount(casePackage.getAssignedCount());
        dto.setAssignedAmount(casePackage.getAssignedAmount());
        dto.setExpectedRecoveryRate(casePackage.getExpectedRecoveryRate());
        dto.setExpectedPeriod(casePackage.getExpectedPeriod());
        dto.setPreferredMethods(JsonUtils.parseJsonList(casePackage.getPreferredMethods(), String.class));
        dto.setAssignmentStrategy(JsonUtils.parseJsonMap(casePackage.getAssignmentStrategy()));
        dto.setStatus(casePackage.getStatus());
        dto.setImportStatus(casePackage.getImportStatus());
        dto.setImportProgress(casePackage.getImportProgress());
        dto.setImportErrorMsg(casePackage.getImportErrorMsg());
        dto.setPublishTime(casePackage.getPublishTime());
        dto.setCreateTime(casePackage.getCreateTime());
        dto.setUpdateTime(casePackage.getUpdateTime());
        return dto;
    }
    
    /**
     * 保存上传的文件
     */
    private String saveUploadedFile(MultipartFile file) {
        // 实现文件保存逻辑，返回文件路径
        // 这里暂时返回一个模拟路径
        return "/tmp/uploads/" + UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
    }
}