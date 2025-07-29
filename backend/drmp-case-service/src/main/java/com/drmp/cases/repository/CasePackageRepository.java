package com.drmp.cases.repository;

import com.drmp.cases.entity.CasePackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 案件包数据访问接口
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Repository
public interface CasePackageRepository extends JpaRepository<CasePackage, Long>, JpaSpecificationExecutor<CasePackage> {
    
    /**
     * 根据案源机构ID查找案件包列表
     */
    List<CasePackage> findBySourceOrgIdAndDeletedFalse(Long sourceOrgId);
    
    /**
     * 根据状态查找案件包
     */
    List<CasePackage> findByStatusAndDeletedFalse(CasePackage.CasePackageStatus status);
    
    /**
     * 分页查询案件包
     */
    @Query("SELECT cp FROM CasePackage cp WHERE cp.deleted = false " +
           "AND (:sourceOrgId IS NULL OR cp.sourceOrgId = :sourceOrgId) " +
           "AND (:status IS NULL OR cp.status = :status) " +
           "AND (:keyword IS NULL OR cp.name LIKE %:keyword% OR cp.description LIKE %:keyword%) " +
           "ORDER BY cp.createTime DESC")
    Page<CasePackage> findByConditions(
            @Param("sourceOrgId") Long sourceOrgId,
            @Param("status") CasePackage.CasePackageStatus status,
            @Param("keyword") String keyword,
            Pageable pageable
    );
    
    /**
     * 查询已发布的案件包
     */
    @Query("SELECT cp FROM CasePackage cp WHERE cp.deleted = false " +
           "AND cp.status IN ('PUBLISHED', 'PROCESSING') " +
           "AND cp.publishTime IS NOT NULL " +
           "ORDER BY cp.publishTime DESC")
    Page<CasePackage> findPublishedCasePackages(Pageable pageable);
    
    /**
     * 更新案件包统计信息
     */
    @Modifying
    @Transactional
    @Query("UPDATE CasePackage cp SET cp.totalCount = :totalCount, cp.totalAmount = :totalAmount " +
           "WHERE cp.id = :id")
    void updateCasePackageStatistics(@Param("id") Long id, 
                                   @Param("totalCount") Integer totalCount, 
                                   @Param("totalAmount") BigDecimal totalAmount);
    
    /**
     * 更新分案统计信息
     */
    @Modifying
    @Transactional
    @Query("UPDATE CasePackage cp SET cp.assignedCount = :assignedCount, " +
           "cp.assignedAmount = :assignedAmount WHERE cp.id = :id")
    void updateAssignmentStatistics(@Param("id") Long id,
                                  @Param("assignedCount") Integer assignedCount,
                                  @Param("assignedAmount") BigDecimal assignedAmount);
    
    /**
     * 更新导入状态
     */
    @Modifying
    @Transactional
    @Query("UPDATE CasePackage cp SET cp.importStatus = :status, cp.importProgress = :progress, " +
           "cp.importErrorMsg = :errorMsg WHERE cp.id = :id")
    void updateImportStatus(@Param("id") Long id,
                          @Param("status") CasePackage.ImportStatus status,
                          @Param("progress") Integer progress,
                          @Param("errorMsg") String errorMsg);
    
    /**
     * 发布案件包
     */
    @Modifying
    @Transactional
    @Query("UPDATE CasePackage cp SET cp.status = :status, cp.publishTime = :publishTime " +
           "WHERE cp.id = :id")
    void publishCasePackage(@Param("id") Long id,
                          @Param("status") CasePackage.CasePackageStatus status,
                          @Param("publishTime") LocalDateTime publishTime);
    
    /**
     * 检查案件包名称是否存在
     */
    @Query("SELECT COUNT(cp) > 0 FROM CasePackage cp WHERE cp.deleted = false " +
           "AND cp.sourceOrgId = :sourceOrgId AND cp.name = :name " +
           "AND (:id IS NULL OR cp.id != :id)")
    boolean existsBySourceOrgIdAndNameAndIdNot(@Param("sourceOrgId") Long sourceOrgId,
                                             @Param("name") String name,
                                             @Param("id") Long id);
    
    /**
     * 统计机构的案件包数量
     */
    @Query("SELECT COUNT(cp) FROM CasePackage cp WHERE cp.deleted = false " +
           "AND cp.sourceOrgId = :sourceOrgId")
    long countBySourceOrgId(@Param("sourceOrgId") Long sourceOrgId);
    
    /**
     * 统计各状态案件包数量
     */
    @Query("SELECT cp.status, COUNT(cp) FROM CasePackage cp WHERE cp.deleted = false " +
           "GROUP BY cp.status")
    List<Object[]> countByStatus();
    
    /**
     * 查询正在导入的案件包
     */
    @Query("SELECT cp FROM CasePackage cp WHERE cp.deleted = false " +
           "AND cp.importStatus = 'PROCESSING'")
    List<CasePackage> findProcessingImports();
    
    /**
     * 查询超时的导入任务
     */
    @Query("SELECT cp FROM CasePackage cp WHERE cp.deleted = false " +
           "AND cp.importStatus = 'PROCESSING' " +
           "AND cp.updateTime < :timeoutTime")
    List<CasePackage> findTimeoutImports(@Param("timeoutTime") LocalDateTime timeoutTime);
    
    /**
     * 根据案源机构和状态统计
     */
    @Query("SELECT COUNT(cp) FROM CasePackage cp WHERE cp.deleted = false " +
           "AND cp.sourceOrgId = :sourceOrgId AND cp.status = :status")
    long countBySourceOrgIdAndStatus(@Param("sourceOrgId") Long sourceOrgId,
                                   @Param("status") CasePackage.CasePackageStatus status);
}