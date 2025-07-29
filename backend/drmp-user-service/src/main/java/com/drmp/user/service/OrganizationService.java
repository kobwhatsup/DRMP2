package com.drmp.user.service;

import com.drmp.common.dto.PageResult;
import com.drmp.common.dto.Result;
import com.drmp.common.enums.OrganizationType;
import com.drmp.user.dto.OrganizationDTO;
import com.drmp.user.entity.Organization;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 机构服务接口
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
public interface OrganizationService {
    
    /**
     * 分页查询机构列表
     *
     * @param type 机构类型
     * @param status 机构状态
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 分页结果
     */
    PageResult<OrganizationDTO> findByConditions(OrganizationType type, 
                                               Organization.OrganizationStatus status,
                                               String keyword, 
                                               Pageable pageable);
    
    /**
     * 根据ID查询机构详情
     *
     * @param id 机构ID
     * @return 机构详情
     */
    OrganizationDTO findById(Long id);
    
    /**
     * 创建机构
     *
     * @param organizationDTO 机构信息
     * @return 创建的机构
     */
    OrganizationDTO create(OrganizationDTO organizationDTO);
    
    /**
     * 更新机构信息
     *
     * @param id 机构ID
     * @param organizationDTO 机构信息
     * @return 更新后的机构
     */
    OrganizationDTO update(Long id, OrganizationDTO organizationDTO);
    
    /**
     * 删除机构（逻辑删除）
     *
     * @param id 机构ID
     */
    void delete(Long id);
    
    /**
     * 机构审核
     *
     * @param id 机构ID
     * @param auditStatus 审核状态
     * @param auditComment 审核意见
     * @return 审核结果
     */
    Result<Void> audit(Long id, Organization.AuditStatus auditStatus, String auditComment);
    
    /**
     * 上传营业执照
     *
     * @param id 机构ID
     * @param file 营业执照文件
     * @return 文件路径
     */
    String uploadBusinessLicense(Long id, MultipartFile file);
    
    /**
     * 上传合同文件
     *
     * @param id 机构ID
     * @param file 合同文件
     * @return 文件路径
     */
    String uploadContractFile(Long id, MultipartFile file);
    
    /**
     * 获取活跃的处置机构列表
     *
     * @return 处置机构列表
     */
    List<OrganizationDTO> findActiveDisposalOrganizations();
    
    /**
     * 根据服务区域查找处置机构
     *
     * @param region 服务区域
     * @return 处置机构列表
     */
    List<OrganizationDTO> findDisposalOrganizationsByRegion(String region);
    
    /**
     * 检查机构名称是否存在
     *
     * @param name 机构名称
     * @param excludeId 排除的机构ID
     * @return 是否存在
     */
    boolean existsByName(String name, Long excludeId);
    
    /**
     * 检查统一社会信用代码是否存在
     *
     * @param code 统一社会信用代码
     * @param excludeId 排除的机构ID
     * @return 是否存在
     */
    boolean existsByUnifiedCreditCode(String code, Long excludeId);
    
    /**
     * 获取待审核机构数量
     *
     * @return 待审核数量
     */
    long countPendingAudit();
}