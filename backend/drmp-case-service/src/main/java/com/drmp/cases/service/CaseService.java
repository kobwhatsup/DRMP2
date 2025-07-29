package com.drmp.cases.service;

import com.drmp.cases.dto.CaseDTO;
import com.drmp.cases.dto.CaseImportDTO;
import com.drmp.common.enums.CaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * 案件服务接口
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
public interface CaseService {
    
    /**
     * 创建案件
     */
    CaseDTO createCase(CaseDTO caseDTO);
    
    /**
     * 更新案件
     */
    CaseDTO updateCase(Long id, CaseDTO caseDTO);
    
    /**
     * 根据ID获取案件
     */
    CaseDTO getCaseById(Long id);
    
    /**
     * 删除案件
     */
    void deleteCase(Long id);
    
    /**
     * 根据借据编号获取案件
     */
    CaseDTO getCaseByReceiptNumber(String receiptNumber);
    
    /**
     * 分页查询案件
     */
    Page<CaseDTO> getCases(Long casePackageId,
                          CaseStatus status,
                          Long assignedOrgId,
                          String keyword,
                          Pageable pageable);
    
    /**
     * 根据案件包ID查询案件
     */
    Page<CaseDTO> getCasesByCasePackageId(Long casePackageId,
                                         CaseStatus status,
                                         String keyword,
                                         Pageable pageable);
    
    /**
     * 获取处置机构的案件
     */
    Page<CaseDTO> getCasesByAssignedOrg(Long orgId,
                                       CaseStatus status,
                                       Pageable pageable);
    
    /**
     * 分配案件给处置机构
     */
    void assignCases(List<Long> caseIds, Long orgId);
    
    /**
     * 更新案件状态
     */
    void updateCaseStatus(Long id, CaseStatus status, String progress);
    
    /**
     * 更新回款信息
     */
    void updateRecoveryInfo(Long id, BigDecimal totalRecovered, BigDecimal recoveryRate);
    
    /**
     * 批量导入案件数据
     */
    List<CaseDTO> batchImportCases(Long casePackageId, List<CaseImportDTO> importData);
    
    /**
     * 验证案件导入数据
     */
    List<CaseImportDTO> validateImportData(List<CaseImportDTO> importData);
    
    /**
     * 解析Excel/CSV文件
     */
    List<CaseImportDTO> parseImportFile(String filePath, String fileName);
    
    /**
     * 检查借据编号是否存在
     */
    boolean existsByReceiptNumber(String receiptNumber, Long excludeId);
    
    /**
     * 获取待分案的案件
     */
    List<CaseDTO> getPendingAssignmentCases();
    
    /**
     * 获取超期未处理的案件
     */
    List<CaseDTO> getOverdueCases(int timeoutDays);
    
    /**
     * 统计案件状态分布
     */
    List<Object[]> getCaseStatusStatistics();
    
    /**
     * 统计处置机构案件分布
     */
    List<Object[]> getOrgCaseStatistics(Long orgId);
    
    /**
     * 获取回款统计
     */
    Object[] getRecoveryStatistics(Long orgId);
    
    /**
     * 根据逾期天数范围查询案件
     */
    List<CaseDTO> getCasesByOverdueDaysRange(Integer minDays, Integer maxDays);
    
    /**
     * 计算逾期等级
     */
    String calculateOverdueLevel(Integer overdueDays);
    
    /**
     * 计算风险等级
     */
    String calculateRiskLevel(CaseDTO caseDTO);
    
    /**
     * 验证案件数据
     */
    void validateCase(CaseDTO caseDTO);
}