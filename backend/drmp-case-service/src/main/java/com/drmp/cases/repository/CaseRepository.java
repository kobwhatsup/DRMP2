package com.drmp.cases.repository;

import com.drmp.cases.entity.Case;
import com.drmp.common.enums.CaseStatus;
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
 * 案件数据访问接口
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Repository
public interface CaseRepository extends JpaRepository<Case, Long>, JpaSpecificationExecutor<Case> {
    
    /**
     * 根据借据编号查找案件
     */
    Optional<Case> findByReceiptNumberAndDeletedFalse(String receiptNumber);
    
    /**
     * 根据案件包ID查找案件列表
     */
    List<Case> findByCasePackageIdAndDeletedFalse(Long casePackageId);
    
    /**
     * 根据案件包ID分页查询案件
     */
    @Query("SELECT c FROM Case c WHERE c.deleted = false " +
           "AND c.casePackageId = :casePackageId " +
           "AND (:status IS NULL OR c.currentStatus = :status) " +
           "AND (:keyword IS NULL OR c.receiptNumber LIKE %:keyword% " +
           "OR c.debtorName LIKE %:keyword% OR c.debtorPhone LIKE %:keyword%) " +
           "ORDER BY c.createTime DESC")
    Page<Case> findByCasePackageIdAndConditions(
            @Param("casePackageId") Long casePackageId,
            @Param("status") CaseStatus status,
            @Param("keyword") String keyword,
            Pageable pageable
    );
    
    /**
     * 分页查询案件
     */
    @Query("SELECT c FROM Case c WHERE c.deleted = false " +
           "AND (:casePackageId IS NULL OR c.casePackageId = :casePackageId) " +
           "AND (:status IS NULL OR c.currentStatus = :status) " +
           "AND (:assignedOrgId IS NULL OR c.assignedOrgId = :assignedOrgId) " +
           "AND (:keyword IS NULL OR c.receiptNumber LIKE %:keyword% " +
           "OR c.debtorName LIKE %:keyword% OR c.debtorPhone LIKE %:keyword%) " +
           "ORDER BY c.createTime DESC")
    Page<Case> findByConditions(
            @Param("casePackageId") Long casePackageId,
            @Param("status") CaseStatus status,
            @Param("assignedOrgId") Long assignedOrgId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
    
    /**
     * 查询处置机构的案件
     */
    @Query("SELECT c FROM Case c WHERE c.deleted = false " +
           "AND c.assignedOrgId = :orgId " +
           "AND (:status IS NULL OR c.currentStatus = :status) " +
           "ORDER BY c.assignedAt DESC")
    Page<Case> findByAssignedOrgId(@Param("orgId") Long orgId,
                                 @Param("status") CaseStatus status,
                                 Pageable pageable);
    
    /**
     * 分配案件给处置机构
     */
    @Modifying
    @Transactional
    @Query("UPDATE Case c SET c.assignedOrgId = :orgId, c.assignedAt = :assignedAt, " +
           "c.currentStatus = :status WHERE c.id IN :caseIds")
    void assignCases(@Param("caseIds") List<Long> caseIds,
                   @Param("orgId") Long orgId,
                   @Param("assignedAt") LocalDateTime assignedAt,
                   @Param("status") CaseStatus status);
    
    /**
     * 更新案件状态
     */
    @Modifying
    @Transactional
    @Query("UPDATE Case c SET c.currentStatus = :status, c.latestProgress = :progress " +
           "WHERE c.id = :id")
    void updateCaseStatus(@Param("id") Long id,
                        @Param("status") CaseStatus status,
                        @Param("progress") String progress);
    
    /**
     * 更新回款信息
     */
    @Modifying
    @Transactional
    @Query("UPDATE Case c SET c.totalRecovered = :totalRecovered, " +
           "c.recoveryRate = :recoveryRate WHERE c.id = :id")
    void updateRecoveryInfo(@Param("id") Long id,
                          @Param("totalRecovered") BigDecimal totalRecovered,
                          @Param("recoveryRate") BigDecimal recoveryRate);
    
    /**
     * 检查借据编号是否存在
     */
    @Query("SELECT COUNT(c) > 0 FROM Case c WHERE c.deleted = false " +
           "AND c.receiptNumber = :receiptNumber AND (:id IS NULL OR c.id != :id)")
    boolean existsByReceiptNumberAndIdNot(@Param("receiptNumber") String receiptNumber,
                                        @Param("id") Long id);
    
    /**
     * 统计案件包的案件数量和金额
     */
    @Query("SELECT COUNT(c), COALESCE(SUM(c.remainingAmount), 0) FROM Case c " +
           "WHERE c.deleted = false AND c.casePackageId = :casePackageId")
    Object[] countAndSumByCasePackageId(@Param("casePackageId") Long casePackageId);
    
    /**
     * 统计已分案的案件数量和金额
     */
    @Query("SELECT COUNT(c), COALESCE(SUM(c.remainingAmount), 0) FROM Case c " +
           "WHERE c.deleted = false AND c.casePackageId = :casePackageId " +
           "AND c.assignedOrgId IS NOT NULL")
    Object[] countAndSumAssignedByCasePackageId(@Param("casePackageId") Long casePackageId);
    
    /**
     * 统计各状态案件数量
     */
    @Query("SELECT c.currentStatus, COUNT(c) FROM Case c WHERE c.deleted = false " +
           "GROUP BY c.currentStatus")
    List<Object[]> countByStatus();
    
    /**
     * 统计处置机构的案件数量
     */
    @Query("SELECT COUNT(c) FROM Case c WHERE c.deleted = false " +
           "AND c.assignedOrgId = :orgId")
    long countByAssignedOrgId(@Param("orgId") Long orgId);
    
    /**
     * 统计处置机构各状态案件数量
     */
    @Query("SELECT c.currentStatus, COUNT(c) FROM Case c WHERE c.deleted = false " +
           "AND c.assignedOrgId = :orgId GROUP BY c.currentStatus")
    List<Object[]> countByAssignedOrgIdAndStatus(@Param("orgId") Long orgId);
    
    /**
     * 查询待分案的案件
     */
    @Query("SELECT c FROM Case c WHERE c.deleted = false " +
           "AND c.currentStatus = 'PENDING_ASSIGNMENT' " +
           "AND c.assignedOrgId IS NULL " +
           "ORDER BY c.createTime")
    List<Case> findPendingAssignmentCases();
    
    /**
     * 查询超期未处理的案件
     */
    @Query("SELECT c FROM Case c WHERE c.deleted = false " +
           "AND c.assignedOrgId IS NOT NULL " +
           "AND c.currentStatus IN ('ASSIGNED', 'PROCESSING') " +
           "AND c.assignedAt < :timeoutTime")
    List<Case> findOverdueCases(@Param("timeoutTime") LocalDateTime timeoutTime);
    
    /**
     * 统计回款情况
     */
    @Query("SELECT COUNT(c), COALESCE(SUM(c.totalRecovered), 0), " +
           "COALESCE(AVG(c.recoveryRate), 0) FROM Case c " +
           "WHERE c.deleted = false AND c.assignedOrgId = :orgId " +
           "AND c.totalRecovered > 0")
    Object[] getRecoveryStatistics(@Param("orgId") Long orgId);
    
    /**
     * 根据逾期天数范围查询案件
     */
    @Query("SELECT c FROM Case c WHERE c.deleted = false " +
           "AND c.overdueDays BETWEEN :minDays AND :maxDays " +
           "ORDER BY c.overdueDays DESC")
    List<Case> findByOverdueDaysRange(@Param("minDays") Integer minDays,
                                    @Param("maxDays") Integer maxDays);
}