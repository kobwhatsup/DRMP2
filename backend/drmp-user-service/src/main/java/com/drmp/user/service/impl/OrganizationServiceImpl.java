package com.drmp.user.service.impl;

import com.drmp.common.dto.PageResult;
import com.drmp.common.dto.Result;
import com.drmp.common.enums.OrganizationType;
import com.drmp.common.exception.BusinessException;
import com.drmp.common.utils.SecurityUtils;
import com.drmp.user.dto.OrganizationDTO;
import com.drmp.user.entity.Organization;
import com.drmp.user.mapper.OrganizationMapper;
import com.drmp.user.repository.OrganizationRepository;
import com.drmp.user.service.FileService;
import com.drmp.user.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 机构服务实现类
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {
    
    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;
    private final FileService fileService;
    
    @Override
    public PageResult<OrganizationDTO> findByConditions(OrganizationType type, 
                                                      Organization.OrganizationStatus status,
                                                      String keyword, 
                                                      Pageable pageable) {
        log.debug("分页查询机构列表，类型：{}，状态：{}，关键词：{}", type, status, keyword);
        
        Page<Organization> page = organizationRepository.findByConditions(type, status, keyword, pageable);
        
        return PageResult.<OrganizationDTO>builder()
                .records(organizationMapper.toDTO(page.getContent()))
                .total(page.getTotalElements())
                .current(page.getNumber() + 1)
                .size(page.getSize())
                .pages(page.getTotalPages())
                .build();
    }
    
    @Override
    public OrganizationDTO findById(Long id) {
        log.debug("查询机构详情，ID：{}", id);
        
        Organization organization = organizationRepository.findById(id)
                .filter(org -> !org.getDeleted())
                .orElseThrow(() -> new BusinessException("机构不存在或已被删除"));
        
        return organizationMapper.toDTO(organization);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrganizationDTO create(OrganizationDTO organizationDTO) {
        log.info("创建机构，名称：{}", organizationDTO.getName());
        
        // 验证机构名称唯一性
        if (existsByName(organizationDTO.getName(), null)) {
            throw new BusinessException("机构名称已存在");
        }
        
        // 验证统一社会信用代码唯一性
        if (StringUtils.hasText(organizationDTO.getUnifiedCreditCode()) &&
            existsByUnifiedCreditCode(organizationDTO.getUnifiedCreditCode(), null)) {
            throw new BusinessException("统一社会信用代码已存在");
        }
        
        // 转换DTO为实体
        Organization organization = organizationMapper.toEntity(organizationDTO);
        organization.setStatus(Organization.OrganizationStatus.PENDING);
        organization.setAuditStatus(Organization.AuditStatus.PENDING);
        
        // 保存机构
        organization = organizationRepository.save(organization);
        
        log.info("机构创建成功，ID：{}，名称：{}", organization.getId(), organization.getName());
        
        return organizationMapper.toDTO(organization);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrganizationDTO update(Long id, OrganizationDTO organizationDTO) {
        log.info("更新机构信息，ID：{}", id);
        
        Organization organization = organizationRepository.findById(id)
                .filter(org -> !org.getDeleted())
                .orElseThrow(() -> new BusinessException("机构不存在或已被删除"));
        
        // 验证机构名称唯一性
        if (!organization.getName().equals(organizationDTO.getName()) &&
            existsByName(organizationDTO.getName(), id)) {
            throw new BusinessException("机构名称已存在");
        }
        
        // 验证统一社会信用代码唯一性
        if (StringUtils.hasText(organizationDTO.getUnifiedCreditCode()) &&
            !organizationDTO.getUnifiedCreditCode().equals(organization.getUnifiedCreditCode()) &&
            existsByUnifiedCreditCode(organizationDTO.getUnifiedCreditCode(), id)) {
            throw new BusinessException("统一社会信用代码已存在");
        }
        
        // 更新机构信息（排除状态和审核相关字段）
        organizationMapper.updateFromDTO(organizationDTO, organization);
        
        // 保存更新
        organization = organizationRepository.save(organization);
        
        log.info("机构信息更新成功，ID：{}，名称：{}", organization.getId(), organization.getName());
        
        return organizationMapper.toDTO(organization);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        log.info("删除机构，ID：{}", id);
        
        Organization organization = organizationRepository.findById(id)
                .filter(org -> !org.getDeleted())
                .orElseThrow(() -> new BusinessException("机构不存在或已被删除"));
        
        // 检查是否有关联的用户
        // TODO: 添加用户关联检查
        
        // 逻辑删除
        organization.setDeleted(true);
        organizationRepository.save(organization);
        
        log.info("机构删除成功，ID：{}，名称：{}", organization.getId(), organization.getName());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> audit(Long id, Organization.AuditStatus auditStatus, String auditComment) {
        log.info("审核机构，ID：{}，状态：{}，意见：{}", id, auditStatus, auditComment);
        
        Organization organization = organizationRepository.findById(id)
                .filter(org -> !org.getDeleted())
                .orElseThrow(() -> new BusinessException("机构不存在或已被删除"));
        
        // 检查当前审核状态
        if (organization.getAuditStatus() != Organization.AuditStatus.PENDING) {
            throw new BusinessException("该机构已经审核过，无法重复审核");
        }
        
        // 更新审核信息
        organization.setAuditStatus(auditStatus);
        organization.setAuditComment(auditComment);
        organization.setAuditTime(LocalDateTime.now());
        organization.setAuditBy(SecurityUtils.getCurrentUserId());
        
        // 根据审核结果更新机构状态
        if (auditStatus == Organization.AuditStatus.APPROVED) {
            organization.setStatus(Organization.OrganizationStatus.ACTIVE);
        } else if (auditStatus == Organization.AuditStatus.REJECTED) {
            organization.setStatus(Organization.OrganizationStatus.REJECTED);
        }
        
        organizationRepository.save(organization);
        
        log.info("机构审核完成，ID：{}，审核结果：{}", id, auditStatus);
        
        return Result.success("审核完成");
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadBusinessLicense(Long id, MultipartFile file) {
        log.info("上传营业执照，机构ID：{}，文件名：{}", id, file.getOriginalFilename());
        
        Organization organization = organizationRepository.findById(id)
                .filter(org -> !org.getDeleted())
                .orElseThrow(() -> new BusinessException("机构不存在或已被删除"));
        
        // 上传文件
        String filePath = fileService.uploadFile(file, "business-license");
        
        // 更新机构信息
        organization.setBusinessLicense(filePath);
        organizationRepository.save(organization);
        
        log.info("营业执照上传成功，机构ID：{}，文件路径：{}", id, filePath);
        
        return filePath;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadContractFile(Long id, MultipartFile file) {
        log.info("上传合同文件，机构ID：{}，文件名：{}", id, file.getOriginalFilename());
        
        Organization organization = organizationRepository.findById(id)
                .filter(org -> !org.getDeleted())
                .orElseThrow(() -> new BusinessException("机构不存在或已被删除"));
        
        // 上传文件
        String filePath = fileService.uploadFile(file, "contract");
        
        // 更新机构信息
        organization.setContractFile(filePath);
        organizationRepository.save(organization);
        
        log.info("合同文件上传成功，机构ID：{}，文件路径：{}", id, filePath);
        
        return filePath;
    }
    
    @Override
    public List<OrganizationDTO> findActiveDisposalOrganizations() {
        log.debug("查询活跃的处置机构列表");
        
        List<Organization> organizations = organizationRepository.findActiveDisposalOrganizations();
        
        return organizationMapper.toDTO(organizations);
    }
    
    @Override
    public List<OrganizationDTO> findDisposalOrganizationsByRegion(String region) {
        log.debug("根据服务区域查找处置机构，区域：{}", region);
        
        List<Organization> organizations = organizationRepository.findDisposalOrganizationsByRegion(region);
        
        return organizationMapper.toDTO(organizations);
    }
    
    @Override
    public boolean existsByName(String name, Long excludeId) {
        return organizationRepository.existsByNameAndIdNot(name, excludeId);
    }
    
    @Override
    public boolean existsByUnifiedCreditCode(String code, Long excludeId) {
        return organizationRepository.existsByUnifiedCreditCodeAndIdNot(code, excludeId);
    }
    
    @Override
    public long countPendingAudit() {
        return organizationRepository.countPendingAudit();
    }
}