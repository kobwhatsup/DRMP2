package com.drmp.cases.service;

import com.drmp.cases.dto.CasePackageDTO;
import com.drmp.cases.dto.BatchImportResult;
import com.drmp.cases.entity.CasePackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 案件包服务接口
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
public interface CasePackageService {
    
    /**
     * 创建案件包
     */
    CasePackageDTO createCasePackage(CasePackageDTO casePackageDTO);
    
    /**
     * 更新案件包
     */
    CasePackageDTO updateCasePackage(Long id, CasePackageDTO casePackageDTO);
    
    /**
     * 根据ID获取案件包
     */
    CasePackageDTO getCasePackageById(Long id);
    
    /**
     * 删除案件包
     */
    void deleteCasePackage(Long id);
    
    /**
     * 分页查询案件包
     */
    Page<CasePackageDTO> getCasePackages(Long sourceOrgId, 
                                       CasePackage.CasePackageStatus status,
                                       String keyword,
                                       Pageable pageable);
    
    /**
     * 获取已发布的案件包
     */
    Page<CasePackageDTO> getPublishedCasePackages(Pageable pageable);
    
    /**
     * 发布案件包
     */
    void publishCasePackage(Long id);
    
    /**
     * 撤回案件包
     */
    void withdrawCasePackage(Long id);
    
    /**
     * 关闭案件包
     */
    void closeCasePackage(Long id);
    
    /**
     * 批量导入案件
     */
    CompletableFuture<BatchImportResult> importCases(Long casePackageId, MultipartFile file);
    
    /**
     * 获取导入进度
     */
    BatchImportResult getImportProgress(String taskId);
    
    /**
     * 更新案件包统计信息
     */
    void updateCasePackageStatistics(Long id);
    
    /**
     * 更新分案统计信息
     */
    void updateAssignmentStatistics(Long id);
    
    /**
     * 检查案件包名称是否存在
     */
    boolean existsBySourceOrgIdAndName(Long sourceOrgId, String name, Long excludeId);
    
    /**
     * 获取机构的案件包统计
     */
    List<Object[]> getOrgCasePackageStatistics(Long sourceOrgId);
    
    /**
     * 获取案件包状态统计
     */
    List<Object[]> getCasePackageStatusStatistics();
    
    /**
     * 处理超时的导入任务
     */
    void processTimeoutImports();
    
    /**
     * 验证案件包数据
     */
    void validateCasePackage(CasePackageDTO casePackageDTO);
}