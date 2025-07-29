package com.drmp.cases.service.impl;

import com.drmp.cases.dto.CaseDTO;
import com.drmp.cases.dto.CaseImportDTO;
import com.drmp.cases.entity.Case;
import com.drmp.cases.repository.CaseRepository;
import com.drmp.cases.service.CaseService;
import com.drmp.common.enums.CaseStatus;
import com.drmp.common.exception.BusinessException;
import com.drmp.common.exception.ErrorCode;
import com.drmp.common.util.EncryptUtils;
import com.drmp.common.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 案件服务实现类
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CaseServiceImpl implements CaseService {
    
    private final CaseRepository caseRepository;
    
    // 身份证号正则表达式
    private static final Pattern ID_CARD_PATTERN = Pattern.compile(
        "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");
    
    // 手机号正则表达式
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    
    @Override
    @Transactional
    public CaseDTO createCase(CaseDTO caseDTO) {
        log.info("创建案件: {}", caseDTO.getReceiptNumber());
        
        // 验证数据
        validateCase(caseDTO);
        
        // 检查借据编号是否重复
        if (existsByReceiptNumber(caseDTO.getReceiptNumber(), null)) {
            throw new BusinessException(ErrorCode.CASE_RECEIPT_NUMBER_EXISTS);
        }
        
        // 创建实体
        Case caseEntity = new Case();
        copyDTOToEntity(caseDTO, caseEntity);
        
        // 设置状态
        caseEntity.setCurrentStatus(CaseStatus.PENDING_ASSIGNMENT);
        caseEntity.setTotalRecovered(BigDecimal.ZERO);
        caseEntity.setRecoveryRate(BigDecimal.ZERO);
        
        caseEntity = caseRepository.save(caseEntity);
        
        log.info("案件创建成功, ID: {}", caseEntity.getId());
        return convertToDTO(caseEntity);
    }
    
    @Override
    @Transactional
    public CaseDTO updateCase(Long id, CaseDTO caseDTO) {
        log.info("更新案件: ID={}", id);
        
        Case caseEntity = caseRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.CASE_NOT_FOUND));
        
        // 验证数据
        validateCase(caseDTO);
        
        // 检查借据编号是否重复
        if (existsByReceiptNumber(caseDTO.getReceiptNumber(), id)) {
            throw new BusinessException(ErrorCode.CASE_RECEIPT_NUMBER_EXISTS);
        }
        
        // 更新字段
        copyDTOToEntity(caseDTO, caseEntity);
        
        caseEntity = caseRepository.save(caseEntity);
        
        log.info("案件更新成功, ID: {}", id);
        return convertToDTO(caseEntity);
    }
    
    @Override
    public CaseDTO getCaseById(Long id) {
        Case caseEntity = caseRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.CASE_NOT_FOUND));
        return convertToDTO(caseEntity);
    }
    
    @Override
    @Transactional
    public void deleteCase(Long id) {
        log.info("删除案件: ID={}", id);
        
        Case caseEntity = caseRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.CASE_NOT_FOUND));
        
        // 检查状态是否允许删除
        if (caseEntity.getCurrentStatus() == CaseStatus.PROCESSING ||
            caseEntity.getCurrentStatus() == CaseStatus.SETTLED) {
            throw new BusinessException(ErrorCode.CASE_CANNOT_DELETE);
        }
        
        // 软删除
        caseEntity.setDeleted(true);
        caseRepository.save(caseEntity);
        
        log.info("案件删除成功, ID: {}", id);
    }
    
    @Override
    public CaseDTO getCaseByReceiptNumber(String receiptNumber) {
        Case caseEntity = caseRepository.findByReceiptNumberAndDeletedFalse(receiptNumber)
            .orElseThrow(() -> new BusinessException(ErrorCode.CASE_NOT_FOUND));
        return convertToDTO(caseEntity);
    }
    
    @Override
    public Page<CaseDTO> getCases(Long casePackageId,
                                 CaseStatus status,
                                 Long assignedOrgId,
                                 String keyword,
                                 Pageable pageable) {
        Page<Case> cases = caseRepository.findByConditions(
            casePackageId, status, assignedOrgId, keyword, pageable);
        return cases.map(this::convertToDTO);
    }
    
    @Override
    public Page<CaseDTO> getCasesByCasePackageId(Long casePackageId,
                                                CaseStatus status,
                                                String keyword,
                                                Pageable pageable) {
        Page<Case> cases = caseRepository.findByCasePackageIdAndConditions(
            casePackageId, status, keyword, pageable);
        return cases.map(this::convertToDTO);
    }
    
    @Override
    public Page<CaseDTO> getCasesByAssignedOrg(Long orgId,
                                              CaseStatus status,
                                              Pageable pageable) {
        Page<Case> cases = caseRepository.findByAssignedOrgId(orgId, status, pageable);
        return cases.map(this::convertToDTO);
    }
    
    @Override
    @Transactional
    public void assignCases(List<Long> caseIds, Long orgId) {
        log.info("分配案件: caseIds={}, orgId={}", caseIds, orgId);
        
        if (caseIds == null || caseIds.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "案件ID列表不能为空");
        }
        
        if (orgId == null) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "处置机构ID不能为空");
        }
        
        LocalDateTime assignedAt = LocalDateTime.now();
        caseRepository.assignCases(caseIds, orgId, assignedAt, CaseStatus.ASSIGNED);
        
        log.info("案件分配成功: {} 个案件分配给机构 {}", caseIds.size(), orgId);
    }
    
    @Override
    @Transactional
    public void updateCaseStatus(Long id, CaseStatus status, String progress) {
        log.info("更新案件状态: ID={}, status={}", id, status);
        
        Case caseEntity = caseRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.CASE_NOT_FOUND));
        
        // 验证状态转换是否合法
        validateStatusTransition(caseEntity.getCurrentStatus(), status);
        
        caseRepository.updateCaseStatus(id, status, progress);
        
        log.info("案件状态更新成功: ID={}, status={}", id, status);
    }
    
    @Override
    @Transactional
    public void updateRecoveryInfo(Long id, BigDecimal totalRecovered, BigDecimal recoveryRate) {
        log.info("更新回款信息: ID={}, totalRecovered={}, recoveryRate={}", 
                id, totalRecovered, recoveryRate);
        
        if (totalRecovered == null || totalRecovered.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "回款金额不能为负数");
        }
        
        if (recoveryRate == null || recoveryRate.compareTo(BigDecimal.ZERO) < 0 ||
            recoveryRate.compareTo(new BigDecimal("100")) > 0) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "回款率必须在0-100之间");
        }
        
        caseRepository.updateRecoveryInfo(id, totalRecovered, recoveryRate);
        
        log.info("回款信息更新成功: ID={}", id);
    }
    
    @Override
    @Transactional
    public List<CaseDTO> batchImportCases(Long casePackageId, List<CaseImportDTO> importData) {
        log.info("批量导入案件: casePackageId={}, count={}", casePackageId, importData.size());
        
        List<CaseDTO> results = new ArrayList<>();
        
        // 过滤有效数据
        List<CaseImportDTO> validData = importData.stream()
            .filter(data -> data.getValid() != null && data.getValid())
            .collect(Collectors.toList());
        
        // 批量插入
        for (CaseImportDTO importDTO : validData) {
            try {
                CaseDTO caseDTO = convertImportDTOToCaseDTO(importDTO, casePackageId);
                CaseDTO savedCase = createCase(caseDTO);
                results.add(savedCase);
            } catch (Exception e) {
                log.error("导入案件失败: receiptNumber={}", importDTO.getReceiptNumber(), e);
                importDTO.setValid(false);
                importDTO.addError("保存失败: " + e.getMessage());
            }
        }
        
        log.info("批量导入案件完成: 成功 {} 条", results.size());
        return results;
    }
    
    @Override
    public List<CaseImportDTO> validateImportData(List<CaseImportDTO> importData) {
        log.info("验证导入数据: count={}", importData.size());
        
        for (CaseImportDTO importDTO : importData) {
            validateImportDTO(importDTO);
        }
        
        long validCount = importData.stream().filter(d -> d.getValid()).count();
        log.info("数据验证完成: 有效 {} 条，无效 {} 条", validCount, importData.size() - validCount);
        
        return importData;
    }
    
    @Override
    public List<CaseImportDTO> parseImportFile(String filePath, String fileName) {
        log.info("解析导入文件: filePath={}, fileName={}", filePath, fileName);
        
        // 这里应该实现具体的文件解析逻辑
        // 根据文件扩展名选择相应的解析器（Excel/CSV等）
        // 暂时返回一个空列表
        List<CaseImportDTO> result = new ArrayList<>();
        
        // TODO: 实现文件解析逻辑
        // if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
        //     result = parseExcelFile(filePath);
        // } else if (fileName.endsWith(".csv")) {
        //     result = parseCsvFile(filePath);
        // }
        
        log.info("文件解析完成: 解析出 {} 条记录", result.size());
        return result;
    }
    
    @Override
    public boolean existsByReceiptNumber(String receiptNumber, Long excludeId) {
        return caseRepository.existsByReceiptNumberAndIdNot(receiptNumber, excludeId);
    }
    
    @Override
    public List<CaseDTO> getPendingAssignmentCases() {
        List<Case> cases = caseRepository.findPendingAssignmentCases();
        return cases.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    @Override
    public List<CaseDTO> getOverdueCases(int timeoutDays) {
        LocalDateTime timeoutTime = LocalDateTime.now().minusDays(timeoutDays);
        List<Case> cases = caseRepository.findOverdueCases(timeoutTime);
        return cases.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    @Override
    public List<Object[]> getCaseStatusStatistics() {
        return caseRepository.countByStatus();
    }
    
    @Override
    public List<Object[]> getOrgCaseStatistics(Long orgId) {
        return caseRepository.countByAssignedOrgIdAndStatus(orgId);
    }
    
    @Override
    public Object[] getRecoveryStatistics(Long orgId) {
        return caseRepository.getRecoveryStatistics(orgId);
    }
    
    @Override
    public List<CaseDTO> getCasesByOverdueDaysRange(Integer minDays, Integer maxDays) {
        List<Case> cases = caseRepository.findByOverdueDaysRange(minDays, maxDays);
        return cases.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    @Override
    public String calculateOverdueLevel(Integer overdueDays) {
        if (overdueDays == null) {
            return "未知";
        }
        
        if (overdueDays <= 30) {
            return "M1";
        } else if (overdueDays <= 60) {
            return "M2";
        } else if (overdueDays <= 90) {
            return "M3";
        } else if (overdueDays <= 180) {
            return "M4-M6";
        } else {
            return "M6+";
        }
    }
    
    @Override
    public String calculateRiskLevel(CaseDTO caseDTO) {
        if (caseDTO.getOverdueDays() == null || caseDTO.getRemainingAmount() == null) {
            return "未知";
        }
        
        int score = 0;
        
        // 逾期天数评分
        if (caseDTO.getOverdueDays() <= 30) {
            score += 1;
        } else if (caseDTO.getOverdueDays() <= 90) {
            score += 2;
        } else if (caseDTO.getOverdueDays() <= 180) {
            score += 3;
        } else {
            score += 4;
        }
        
        // 金额评分
        if (caseDTO.getRemainingAmount().compareTo(new BigDecimal("10000")) <= 0) {
            score += 1;
        } else if (caseDTO.getRemainingAmount().compareTo(new BigDecimal("50000")) <= 0) {
            score += 2;
        } else if (caseDTO.getRemainingAmount().compareTo(new BigDecimal("100000")) <= 0) {
            score += 3;
        } else {
            score += 4;
        }
        
        // 根据总分确定风险等级
        if (score <= 2) {
            return "低风险";
        } else if (score <= 4) {
            return "中风险";
        } else if (score <= 6) {
            return "高风险";
        } else {
            return "极高风险";
        }
    }
    
    @Override
    public void validateCase(CaseDTO caseDTO) {
        if (caseDTO.getCasePackageId() == null) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "案件包ID不能为空");
        }
        
        if (caseDTO.getReceiptNumber() == null || caseDTO.getReceiptNumber().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "借据编号不能为空");
        }
        
        if (caseDTO.getDebtorIdCard() == null || caseDTO.getDebtorIdCard().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "身份证号不能为空");
        }
        
        if (!ID_CARD_PATTERN.matcher(caseDTO.getDebtorIdCard()).matches()) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "身份证号格式不正确");
        }
        
        if (caseDTO.getDebtorName() == null || caseDTO.getDebtorName().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "客户姓名不能为空");
        }
        
        if (caseDTO.getDebtorPhone() == null || caseDTO.getDebtorPhone().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "手机号不能为空");
        }
        
        if (!PHONE_PATTERN.matcher(caseDTO.getDebtorPhone()).matches()) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "手机号格式不正确");
        }
        
        if (caseDTO.getLoanAmount() == null || caseDTO.getLoanAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "贷款金额必须大于0");
        }
        
        if (caseDTO.getRemainingAmount() == null || caseDTO.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "剩余应还金额必须大于0");
        }
        
        if (caseDTO.getOverdueDays() == null || caseDTO.getOverdueDays() < 0) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "逾期天数不能为负数");
        }
        
        if (caseDTO.getConsignStartDate() == null || caseDTO.getConsignEndDate() == null) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "委托开始时间和到期时间不能为空");
        }
        
        if (caseDTO.getConsignStartDate().isAfter(caseDTO.getConsignEndDate())) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "委托开始时间不能晚于到期时间");
        }
    }
    
    /**
     * 验证导入数据
     */
    private void validateImportDTO(CaseImportDTO importDTO) {
        importDTO.setValid(true);
        
        // 验证必填字段
        if (importDTO.getReceiptNumber() == null || importDTO.getReceiptNumber().trim().isEmpty()) {
            importDTO.addError("借据编号不能为空");
        }
        
        if (importDTO.getDebtorIdCard() == null || importDTO.getDebtorIdCard().trim().isEmpty()) {
            importDTO.addError("身份证号不能为空");
        } else if (!ID_CARD_PATTERN.matcher(importDTO.getDebtorIdCard()).matches()) {
            importDTO.addError("身份证号格式不正确");
        }
        
        if (importDTO.getDebtorName() == null || importDTO.getDebtorName().trim().isEmpty()) {
            importDTO.addError("客户姓名不能为空");
        }
        
        if (importDTO.getDebtorPhone() == null || importDTO.getDebtorPhone().trim().isEmpty()) {
            importDTO.addError("手机号不能为空");
        } else if (!PHONE_PATTERN.matcher(importDTO.getDebtorPhone()).matches()) {
            importDTO.addError("手机号格式不正确");
        }
        
        if (importDTO.getLoanProduct() == null || importDTO.getLoanProduct().trim().isEmpty()) {
            importDTO.addError("借款项目不能为空");
        }
        
        if (importDTO.getLoanAmount() == null || importDTO.getLoanAmount().compareTo(BigDecimal.ZERO) <= 0) {
            importDTO.addError("贷款金额必须大于0");
        }
        
        if (importDTO.getRemainingAmount() == null || importDTO.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
            importDTO.addError("剩余应还金额必须大于0");
        }
        
        if (importDTO.getOverdueDays() == null || importDTO.getOverdueDays() < 0) {
            importDTO.addError("逾期天数不能为负数");
        }
        
        if (importDTO.getConsigner() == null || importDTO.getConsigner().trim().isEmpty()) {
            importDTO.addError("委托方不能为空");
        }
        
        if (importDTO.getConsignStartDate() == null) {
            importDTO.addError("委托开始时间不能为空");
        }
        
        if (importDTO.getConsignEndDate() == null) {
            importDTO.addError("委托到期时间不能为空");
        }
        
        if (importDTO.getConsignStartDate() != null && importDTO.getConsignEndDate() != null &&
            importDTO.getConsignStartDate().isAfter(importDTO.getConsignEndDate())) {
            importDTO.addError("委托开始时间不能晚于到期时间");
        }
        
        if (importDTO.getFundProvider() == null || importDTO.getFundProvider().trim().isEmpty()) {
            importDTO.addError("资方名称不能为空");
        }
        
        // 检查借据编号是否重复
        if (importDTO.getReceiptNumber() != null && existsByReceiptNumber(importDTO.getReceiptNumber(), null)) {
            importDTO.addError("借据编号已存在");
        }
    }
    
    /**
     * 验证状态转换
     */
    private void validateStatusTransition(CaseStatus currentStatus, CaseStatus newStatus) {
        // 定义允许的状态转换
        switch (currentStatus) {
            case PENDING_ASSIGNMENT:
                if (newStatus != CaseStatus.ASSIGNED && newStatus != CaseStatus.CLOSED) {
                    throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION);
                }
                break;
            case ASSIGNED:
                if (newStatus != CaseStatus.PROCESSING && newStatus != CaseStatus.CLOSED) {
                    throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION);
                }
                break;
            case PROCESSING:
                if (newStatus != CaseStatus.SETTLED && newStatus != CaseStatus.LITIGATION && 
                    newStatus != CaseStatus.CLOSED) {
                    throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION);
                }
                break;
            case SETTLED:
            case LITIGATION:
                if (newStatus != CaseStatus.CLOSED) {
                    throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION);
                }
                break;
            case CLOSED:
                throw new BusinessException(ErrorCode.CASE_ALREADY_CLOSED);
            default:
                throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION);
        }
    }
    
    /**
     * 导入DTO转案件DTO
     */
    private CaseDTO convertImportDTOToCaseDTO(CaseImportDTO importDTO, Long casePackageId) {
        CaseDTO caseDTO = new CaseDTO();
        caseDTO.setCasePackageId(casePackageId);
        caseDTO.setReceiptNumber(importDTO.getReceiptNumber());
        caseDTO.setDebtorIdCard(importDTO.getDebtorIdCard());
        caseDTO.setDebtorName(importDTO.getDebtorName());
        caseDTO.setDebtorPhone(importDTO.getDebtorPhone());
        caseDTO.setLoanProduct(importDTO.getLoanProduct());
        caseDTO.setLoanAmount(importDTO.getLoanAmount());
        caseDTO.setRemainingAmount(importDTO.getRemainingAmount());
        caseDTO.setOverdueDays(importDTO.getOverdueDays());
        caseDTO.setConsigner(importDTO.getConsigner());
        caseDTO.setConsignStartDate(importDTO.getConsignStartDate());
        caseDTO.setConsignEndDate(importDTO.getConsignEndDate());
        caseDTO.setFundProvider(importDTO.getFundProvider());
        
        // 设置JSON字段
        if (importDTO.getDebtFields() != null) {
            caseDTO.setDebtInfo(JsonUtils.toJsonString(importDTO.getDebtFields()));
        }
        if (importDTO.getDebtorFields() != null) {
            caseDTO.setDebtorInfo(JsonUtils.toJsonString(importDTO.getDebtorFields()));
        }
        if (importDTO.getContactFields() != null) {
            caseDTO.setContactInfo(JsonUtils.toJsonString(importDTO.getContactFields()));
        }
        if (importDTO.getCustomFields() != null) {
            caseDTO.setCustomFields(JsonUtils.toJsonString(importDTO.getCustomFields()));
        }
        
        return caseDTO;
    }
    
    /**
     * DTO复制到实体
     */
    private void copyDTOToEntity(CaseDTO caseDTO, Case caseEntity) {
        caseEntity.setCasePackageId(caseDTO.getCasePackageId());
        caseEntity.setReceiptNumber(caseDTO.getReceiptNumber());
        
        // 加密敏感信息
        caseEntity.setDebtorIdCard(EncryptUtils.encrypt(caseDTO.getDebtorIdCard()));
        caseEntity.setDebtorName(EncryptUtils.encrypt(caseDTO.getDebtorName()));
        caseEntity.setDebtorPhone(EncryptUtils.encrypt(caseDTO.getDebtorPhone()));
        
        caseEntity.setLoanProduct(caseDTO.getLoanProduct());
        caseEntity.setLoanAmount(caseDTO.getLoanAmount());
        caseEntity.setRemainingAmount(caseDTO.getRemainingAmount());
        caseEntity.setOverdueDays(caseDTO.getOverdueDays());
        caseEntity.setConsigner(caseDTO.getConsigner());
        caseEntity.setConsignStartDate(caseDTO.getConsignStartDate());
        caseEntity.setConsignEndDate(caseDTO.getConsignEndDate());
        caseEntity.setFundProvider(caseDTO.getFundProvider());
        caseEntity.setDebtInfo(caseDTO.getDebtInfo());
        caseEntity.setDebtorInfo(caseDTO.getDebtorInfo());
        caseEntity.setContactInfo(caseDTO.getContactInfo());
        caseEntity.setCustomFields(caseDTO.getCustomFields());
        caseEntity.setLatestProgress(caseDTO.getLatestProgress());
        caseEntity.setAttachments(JsonUtils.toJsonString(caseDTO.getAttachments()));
    }
    
    /**
     * 实体转DTO
     */
    private CaseDTO convertToDTO(Case caseEntity) {
        CaseDTO dto = new CaseDTO();
        dto.setId(caseEntity.getId());
        dto.setCasePackageId(caseEntity.getCasePackageId());
        dto.setReceiptNumber(caseEntity.getReceiptNumber());
        
        // 解密敏感信息
        dto.setDebtorIdCard(EncryptUtils.decrypt(caseEntity.getDebtorIdCard()));
        dto.setDebtorName(EncryptUtils.decrypt(caseEntity.getDebtorName()));
        dto.setDebtorPhone(EncryptUtils.decrypt(caseEntity.getDebtorPhone()));
        
        dto.setLoanProduct(caseEntity.getLoanProduct());
        dto.setLoanAmount(caseEntity.getLoanAmount());
        dto.setRemainingAmount(caseEntity.getRemainingAmount());
        dto.setOverdueDays(caseEntity.getOverdueDays());
        dto.setOverdueLevel(calculateOverdueLevel(caseEntity.getOverdueDays()));
        dto.setConsigner(caseEntity.getConsigner());
        dto.setConsignStartDate(caseEntity.getConsignStartDate());
        dto.setConsignEndDate(caseEntity.getConsignEndDate());
        dto.setFundProvider(caseEntity.getFundProvider());
        dto.setDebtInfo(caseEntity.getDebtInfo());
        dto.setDebtorInfo(caseEntity.getDebtorInfo());
        dto.setContactInfo(caseEntity.getContactInfo());
        dto.setCustomFields(caseEntity.getCustomFields());
        dto.setCurrentStatus(caseEntity.getCurrentStatus());
        dto.setAssignedOrgId(caseEntity.getAssignedOrgId());
        dto.setAssignedAt(caseEntity.getAssignedAt());
        dto.setLatestProgress(caseEntity.getLatestProgress());
        dto.setTotalRecovered(caseEntity.getTotalRecovered());
        dto.setRecoveryRate(caseEntity.getRecoveryRate());
        dto.setAttachments(JsonUtils.parseJsonList(caseEntity.getAttachments(), String.class));
        dto.setRiskLevel(calculateRiskLevel(dto));
        
        // 设置状态标志
        dto.setAssigned(caseEntity.getAssignedOrgId() != null);
        dto.setProcessing(caseEntity.getCurrentStatus() == CaseStatus.PROCESSING);
        dto.setClosed(caseEntity.getCurrentStatus() == CaseStatus.CLOSED);
        
        dto.setCreateTime(caseEntity.getCreateTime());
        dto.setUpdateTime(caseEntity.getUpdateTime());
        
        return dto;
    }
}