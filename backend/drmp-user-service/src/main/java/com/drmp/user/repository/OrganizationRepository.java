package com.drmp.user.repository;

import com.drmp.common.enums.OrganizationType;
import com.drmp.user.entity.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 机构数据访问接口
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long>, JpaSpecificationExecutor<Organization> {
    
    /**
     * 根据机构名称查找机构
     */
    Optional<Organization> findByNameAndDeletedFalse(String name);
    
    /**
     * 根据统一社会信用代码查找机构
     */
    Optional<Organization> findByUnifiedCreditCodeAndDeletedFalse(String unifiedCreditCode);
    
    /**
     * 根据类型查找机构列表
     */
    List<Organization> findByTypeAndStatusAndDeletedFalse(
            OrganizationType type, 
            Organization.OrganizationStatus status
    );
    
    /**
     * 分页查询机构
     */
    @Query("SELECT o FROM Organization o WHERE o.deleted = false " +
           "AND (:type IS NULL OR o.type = :type) " +
           "AND (:status IS NULL OR o.status = :status) " +
           "AND (:keyword IS NULL OR o.name LIKE %:keyword% OR o.contactPerson LIKE %:keyword%)")
    Page<Organization> findByConditions(
            @Param("type") OrganizationType type,
            @Param("status") Organization.OrganizationStatus status,
            @Param("keyword") String keyword,
            Pageable pageable
    );
    
    /**
     * 查询待审核的机构数量
     */
    @Query("SELECT COUNT(o) FROM Organization o WHERE o.deleted = false " +
           "AND o.auditStatus = com.drmp.user.entity.Organization$AuditStatus.PENDING")
    long countPendingAudit();
    
    /**
     * 查询活跃的处置机构
     */
    @Query("SELECT o FROM Organization o WHERE o.deleted = false " +
           "AND o.type = com.drmp.common.enums.OrganizationType.DISPOSAL " +
           "AND o.status = com.drmp.user.entity.Organization$OrganizationStatus.ACTIVE " +
           "ORDER BY o.createTime DESC")
    List<Organization> findActiveDisposalOrganizations();
    
    /**
     * 根据服务区域查找处置机构
     */
    @Query("SELECT o FROM Organization o WHERE o.deleted = false " +
           "AND o.type = com.drmp.common.enums.OrganizationType.DISPOSAL " +
           "AND o.status = com.drmp.user.entity.Organization$OrganizationStatus.ACTIVE " +
           "AND JSON_CONTAINS(o.serviceRegions, JSON_QUOTE(:region))")
    List<Organization> findDisposalOrganizationsByRegion(@Param("region") String region);
    
    /**
     * 检查机构名称是否已存在
     */
    @Query("SELECT COUNT(o) > 0 FROM Organization o WHERE o.deleted = false " +
           "AND o.name = :name AND (:id IS NULL OR o.id != :id)")
    boolean existsByNameAndIdNot(@Param("name") String name, @Param("id") Long id);
    
    /**
     * 检查统一社会信用代码是否已存在
     */
    @Query("SELECT COUNT(o) > 0 FROM Organization o WHERE o.deleted = false " +
           "AND o.unifiedCreditCode = :code AND (:id IS NULL OR o.id != :id)")
    boolean existsByUnifiedCreditCodeAndIdNot(@Param("code") String code, @Param("id") Long id);
}