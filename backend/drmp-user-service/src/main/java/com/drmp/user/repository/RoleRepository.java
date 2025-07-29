package com.drmp.user.repository;

import com.drmp.user.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 角色数据访问接口
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * 根据角色编码查找角色
     */
    Optional<Role> findByCodeAndDeletedFalse(String code);
    
    /**
     * 根据机构类型查找角色列表
     */
    List<Role> findByOrgTypeAndDeletedFalse(Role.OrgType orgType);
    
    /**
     * 查找默认角色列表
     */
    List<Role> findByIsDefaultTrueAndDeletedFalse();
    
    /**
     * 根据机构类型查找默认角色
     */
    List<Role> findByOrgTypeAndIsDefaultTrueAndDeletedFalse(Role.OrgType orgType);
    
    /**
     * 分页查询角色
     */
    @Query("SELECT r FROM Role r WHERE r.deleted = false " +
           "AND (:orgType IS NULL OR r.orgType = :orgType) " +
           "AND (:keyword IS NULL OR r.name LIKE %:keyword% OR r.code LIKE %:keyword%) " +
           "ORDER BY r.sortOrder ASC, r.createTime DESC")
    Page<Role> findByConditions(@Param("orgType") Role.OrgType orgType,
                               @Param("keyword") String keyword,
                               Pageable pageable);
    
    /**
     * 检查角色编码是否已存在
     */
    @Query("SELECT COUNT(r) > 0 FROM Role r WHERE r.deleted = false " +
           "AND r.code = :code AND (:id IS NULL OR r.id != :id)")
    boolean existsByCodeAndIdNot(@Param("code") String code, @Param("id") Long id);
    
    /**
     * 检查角色名称是否已存在
     */
    @Query("SELECT COUNT(r) > 0 FROM Role r WHERE r.deleted = false " +
           "AND r.name = :name AND (:id IS NULL OR r.id != :id)")
    boolean existsByNameAndIdNot(@Param("name") String name, @Param("id") Long id);
}